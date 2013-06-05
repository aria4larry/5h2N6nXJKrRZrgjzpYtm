package com.droidcat.stackranger.ui;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.droidcat.stackranger.R;
import com.droidcat.stackranger.util.Utilis;
import net.sf.jtpl.Template;
import net.sf.stackwrap4j.entities.Answer;
import net.sf.stackwrap4j.entities.Comment;
import net.sf.stackwrap4j.entities.User;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.utils.StackUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

@SuppressLint("HandlerLeak")
public class AnswerFragment extends Fragment {
    private static final String LOG_TAG = AnswerFragment.class
            .getSimpleName();
    private static String sTemplateString;
    int mAnswerIndex = 0;
    List<Answer> mAnswers;
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private String mEndPoint = "";
    private boolean mHaveComment = false;
    private HashMap<String, String> answerWebsourceMap;

    public AnswerFragment(List<Answer> answers) {
        mAnswers = answers;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AnswerFragment.sTemplateString == null) {
            try {
                InputStream iStream = getActivity().getAssets().open(
                        "question.html", AssetManager.ACCESS_BUFFER);
                StringBuilder sBuilder = new StringBuilder();
                byte[] buffer = new byte[1024 * 8];
                int readLen;
                while ((readLen = iStream.read(buffer)) != -1) {
                    sBuilder.append(new String(buffer, 0, readLen));
                }
                AnswerFragment.sTemplateString = sBuilder.toString();
                Log.i(LOG_TAG, AnswerFragment.sTemplateString);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        answerWebsourceMap = new HashMap<String, String>();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProgressBar = (ProgressBar) getActivity().findViewById(R.id.progressBar);
        mProgressBar.setIndeterminate(true);
        mWebView = (WebView) getActivity().findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new SampleWebViewClient());
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        if (getArguments() != null && getArguments().containsKey(QuestionsFragment.ARG_SITE)) {
            mEndPoint = getArguments().getString(QuestionsFragment.ARG_SITE);
        }
        showNextAnswer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.answer, container, false);
    }

    private void showNextAnswer() {
        if (mAnswers != null && mAnswerIndex < mAnswers.size()) {
            Answer answer = mAnswers.get(mAnswerIndex);
            String webSource = answerWebsourceMap.get(Integer.toString(answer.getPostId()));
            if (webSource == null) {
                webSource = loadAnswer(answer);
                answerWebsourceMap.put(Integer.toString(answer.getPostId()), webSource);
            }
            Log.i(LOG_TAG, webSource);
            mWebView.loadDataWithBaseURL("about:blank", webSource, "text/html",
                    "utf-8", null);
            mAnswerIndex++;
        } else {
            Utilis.showToast(getActivity(), "No more answers", Toast.LENGTH_SHORT);
        }
    }

    private void showPreviousAnswer() {
        mAnswerIndex--;
        if (mAnswers != null && mAnswerIndex >= 0) {
            Answer answer = mAnswers.get(mAnswerIndex);
            String webSource = answerWebsourceMap.get(Integer.toString(answer.getPostId()));
            if (webSource == null) {
                webSource = loadAnswer(answer);
                answerWebsourceMap.put(Integer.toString(answer.getPostId()), webSource);
            }
            Log.i(LOG_TAG, webSource);
            mWebView.loadDataWithBaseURL("about:blank", webSource, "text/html",
                    "utf-8", null);
        } else {
            Utilis.showToast(getActivity(), "No more answers", Toast.LENGTH_SHORT);
        }
    }

    private String loadAnswer(Answer answer) {
        Template template = new Template(sTemplateString);
        template.assign("TITLE", "");
        template.parse("main.title");
        try {
            template.assign("QBODY", answer.getBody());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        User user = answer.getOwner();
        template.assign("QSCORE", String.valueOf(answer.getScore()));
        template.assign("QAHASH", String.valueOf(user.getEmailHash()));
        template.assign("QANAME", user.getDisplayName());
        template.assign("QAREP", StackUtils.formatRep(user.getReputation()));
        template.assign("QAID", String.valueOf(user.getId()));
        template.assign("ENDPOINT", mEndPoint);
        template.assign("QWHEN",
                StackUtils.formatElapsedTime(answer.getCreationDate()));
        try {
            if (answer.getComments() != null) {
                for (Comment comment : answer.getComments()) {
                    template.assign("CBODY", comment.getBody());
                    User user2 = comment.getOwner();
                    if (user2 != null) {
                        template.assign("CAUTHOR", user2.getDisplayName());
                        template.assign("CAID", Integer.toString(user2.getId()));
                        template.assign("CSCORE",
                                String.valueOf(comment.getScore()));
                        if (comment.getScore() > 0) {
                            template.parse("main.post.comment.score");
                        }
                        template.parse("main.post.comment");
                    }
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        template.parse("main.post");
        template.assign("FONTSIZE", "1em");
        template.parse("main");
        return template.out();
    }

    public interface onAnswerLoaded {
        void onAnswerLoaded(List<Answer> answers);
    }

    private static class SampleWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}

package com.droidcat.stackranger.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.sf.jtpl.Template;
import net.sf.stackwrap4j.entities.Comment;
import net.sf.stackwrap4j.entities.Question;
import net.sf.stackwrap4j.entities.User;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.utils.StackUtils;
import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.droidcat.stackranger.R;
import com.droidcat.stackranger.newwork.AsyncTaskGetQuestion;

@SuppressLint("HandlerLeak")
public class QuestionFragment extends Fragment {
    private WebView mWebView;
    public static final String KEY_QUESTION_ID = "QUESTION_ID";
    private Question mQuestion;
    private int mQuestionId;
    private static String sTemplateString;
    private static final String LOG_TAG = QuestionFragment.class
            .getSimpleName();
    private String mEndPoint;
    private boolean mHaveComment = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(LOG_TAG, "handleMessage question~~~");
            Log.d(LOG_TAG, "msg.obj question~~~" + msg.obj);
            if (msg.obj != null) {
                Log.d(LOG_TAG, "msg.obj != null~");
                mQuestion = (Question) msg.obj;
                mHaveComment = msg.what == 1;
                updateView();
            }
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
        mHandler = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (QuestionFragment.sTemplateString == null) {
            try {
                InputStream iStream = getActivity().getAssets().open(
                        "question.html", AssetManager.ACCESS_BUFFER);
                StringBuilder sBuilder = new StringBuilder();
                byte[] buffer = new byte[1024 * 8];
                int readLen;
                while ((readLen = iStream.read(buffer)) != -1) {
                    sBuilder.append(new String(buffer, 0, readLen));
                }
                QuestionFragment.sTemplateString = sBuilder.toString();
                Log.i(LOG_TAG, QuestionFragment.sTemplateString);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public QuestionFragment() {

    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mWebView = (WebView) getActivity().findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new SampleWebViewClient());
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        if (getArguments().containsKey(KEY_QUESTION_ID)
                && getArguments().containsKey(QuestionsFragment.ARG_SITE)) {
            mQuestionId = getArguments().getInt(KEY_QUESTION_ID);
            mEndPoint = getArguments().getString(QuestionsFragment.ARG_SITE);
            new AsyncTaskGetQuestion(getActivity(), mHandler, mEndPoint,
                    mQuestionId).execute(mEndPoint);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.question, container, false);
    }

    private void updateView() {
        Log.d(LOG_TAG, "updateView");
        getActivity().setTitle(mQuestion.getTitle());
        Template template = new Template(sTemplateString);
        template.assign("TITLE", mQuestion.getTitle());
        template.parse("main.title");
        try {
            template.assign("QBODY", mQuestion.getBody());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        User user = this.mQuestion.getOwner();
        template.assign("QSCORE", String.valueOf(mQuestion.getScore()));
        template.assign("QAHASH", String.valueOf(user.getEmailHash()));
        template.assign("QANAME", user.getDisplayName());
        template.assign("QAREP", StackUtils.formatRep(user.getReputation()));
        template.assign("QAID", String.valueOf(user.getId()));
        template.assign("ENDPOINT", mEndPoint);
        template.assign("QWHEN",
                StackUtils.formatElapsedTime(this.mQuestion.getCreationDate()));
        List<String> tags = (ArrayList<String>) mQuestion.getTags();
        for (String tag : tags) {
            template.assign("TAG", tag);
            template.parse("main.post.tags.tag");
        }
        template.parse("main.post.tags");
        try {
            if (mHaveComment) {
                for (Comment comment : mQuestion.getComments()) {
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
        String webSource = template.out();
        Log.i(LOG_TAG, webSource);
        mWebView.loadDataWithBaseURL("about:blank", webSource, "text/html",
                "utf-8", null);
    }

    private static class SampleWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}

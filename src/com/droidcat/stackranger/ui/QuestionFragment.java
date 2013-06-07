package com.droidcat.stackranger.ui;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.droidcat.stackranger.R;
import com.droidcat.stackranger.newwork.AsyncTaskGetQuestion;

import net.sf.jtpl.Template;
import net.sf.stackwrap4j.entities.Comment;
import net.sf.stackwrap4j.entities.Question;
import net.sf.stackwrap4j.entities.User;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.utils.StackUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@SuppressLint("HandlerLeak")
public class QuestionFragment extends Fragment implements View.OnClickListener {
    public static final String KEY_QUESTION_ID = "QUESTION_ID";
    private static final String LOG_TAG = QuestionFragment.class
            .getSimpleName();
    private static String sTemplateString;
    String questinWebSource = null;
    AnswerFragment.onAnswerLoaded mOnAnswerLoaded;
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private TextView mAnswerCount;
    private Question mQuestion;
    private int mQuestionId;
    private String mEndPoint;
    private ImageView btn_back;
    private boolean mHaveComment = false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AsyncTaskGetQuestion.MSG_LOADING:
                    mProgressBar.setVisibility(View.VISIBLE);
                    break;
                default:
                    Log.d(LOG_TAG, "handleMessage question~~~");
                    Log.d(LOG_TAG, "msg.obj question~~~" + msg.obj);
                    if (msg.obj != null && !isDetached()) {
                        Log.d(LOG_TAG, "msg.obj != null~");
                        mQuestion = (Question) msg.obj;
                        mHaveComment = msg.what == AsyncTaskGetQuestion.MSG_HAVE_COMMENT;
                        if (mQuestion.getAnswer_count() > 0) {
                            try {
                                mOnAnswerLoaded.onAnswerLoaded(mQuestion.getAnswers());
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        mAnswerCount.setVisibility(View.VISIBLE);
                        mAnswerCount.setText(Integer.toString(mQuestion.getAnswer_count()));
                        //TODO:null pointer happend when calling this
                        //if the activity is destroyed
                        showQuestion();
                    }
            }
        }
    };

    public QuestionFragment() {

    }

    public void setOnAnswerLoadedCallBack(AnswerFragment.onAnswerLoaded onAnswerLoadedCallBack) {
        mOnAnswerLoaded = onAnswerLoadedCallBack;
    }

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

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProgressBar = (ProgressBar) getActivity().findViewById(R.id.top_progress);
        mAnswerCount = (TextView) getActivity().findViewById(R.id.top_message_count);
        mAnswerCount.setOnClickListener(this);
        mWebView = (WebView) getActivity().findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new SampleWebViewClient());
        WebSettings webSettings = mWebView.getSettings();
        btn_back = (ImageView) getActivity().findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
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

    private void showQuestion() {
        Log.d(LOG_TAG, "updateView");
        getActivity().setTitle(mQuestion.getTitle());
        if (questinWebSource == null) {
            questinWebSource = loadQuestion(mQuestion);
        }
        Log.i(LOG_TAG, questinWebSource);
        mWebView.loadDataWithBaseURL("about:blank", questinWebSource, "text/html",
                "utf-8", null);
        mProgressBar.setVisibility(View.GONE);
    }

    private String loadQuestion(Question question) {
        Template template = new Template(sTemplateString);
        template.assign("TITLE", question.getTitle());
        template.parse("main.title");
        try {
            template.assign("QBODY", question.getBody());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        User user = question.getOwner();
        template.assign("QSCORE", String.valueOf(question.getScore()));
        template.assign("QAHASH", String.valueOf(user.getEmailHash()));
        template.assign("QANAME", user.getDisplayName());
        template.assign("QAREP", StackUtils.formatRep(user.getReputation()));
        template.assign("QAID", String.valueOf(user.getId()));
        template.assign("ENDPOINT", mEndPoint);
        template.assign("QWHEN",
                StackUtils.formatElapsedTime(question.getCreationDate()));
        List<String> tags = question.getTags();
        for (String tag : tags) {
            template.assign("TAG", tag);
            template.parse("main.post.tags.tag");
        }
        template.parse("main.post.tags");
        try {
            if (mHaveComment) {
                for (Comment comment : question.getComments()) {
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                getActivity().onBackPressed();
                break;
            case R.id.top_message_count:
                ((QuestionActivity)getActivity()).toggle();
                break;
        }
    }

    private static class SampleWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}

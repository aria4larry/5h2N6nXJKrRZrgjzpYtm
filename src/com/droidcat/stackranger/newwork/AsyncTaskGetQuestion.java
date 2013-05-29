package com.droidcat.stackranger.newwork;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import com.droidcat.stackranger.util.Config;
import net.sf.stackwrap4j.StackWrapper;
import net.sf.stackwrap4j.entities.Question;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.query.QuestionQuery;

import java.io.IOException;

public class AsyncTaskGetQuestion extends AsyncTask<String, Integer, Question> {
    public static final String LOG_TAG = AsyncTaskGetQuestion.class
            .getSimpleName();
    private static final String FILTER_STRING = "!)65Ubs6S4uRBWEq-kouuN3hIFwl.";
    public static final int MSG_HAVE_COMMENT = 1;
    public static final int MSG_NO_COMMENT = 2;
    public static final int MSG_LOADING = 3;
    public static final int MSG_UPDATE_PROGRESS = 4;
    private Handler mHandler;
    private String mUrl;
    private int mQuestionId;
    private int mWhat;

    public AsyncTaskGetQuestion(Context context, Handler handler, String url,
                                int questionId) {
        super();
        mHandler = handler;
        mUrl = url;
        mQuestionId = questionId;
        Log.i(LOG_TAG, "parameters: mUrl = " + mUrl + " mQuestionID = "
                + mQuestionId);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mHandler.handleMessage(mHandler.obtainMessage(MSG_LOADING));
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        mHandler.handleMessage(mHandler.obtainMessage(MSG_UPDATE_PROGRESS,values[0]));
    }

    @Override
    protected void onPostExecute(Question result) {
        Log.i(LOG_TAG, "List<Question> result = " + result);
        mHandler.handleMessage(mHandler.obtainMessage(mWhat, result));
    }

    @Override
    protected Question doInBackground(String... params) {
        onProgressUpdate(5);
        StackWrapper stackWrapper = new StackWrapper(mUrl, Config.APIKEY);
        QuestionQuery qq = new QuestionQuery();
        qq.setFilter(FILTER_STRING);
        Log.i("error", qq.getUrlParams());
        mWhat = MSG_NO_COMMENT;// no comments yet~
        Question question = null;
        try {
            onProgressUpdate(20);
            question = stackWrapper.getQuestionById(mQuestionId,
                    qq.getUrlParams());
            onProgressUpdate(35);
            Log.i(LOG_TAG, question.getBody());
            onProgressUpdate(70);
            Log.i(LOG_TAG, "getAnswers" + question.getAnswers());
            Log.i(LOG_TAG, "getComments" + question.getComments());
            onProgressUpdate(80);
            if (question.getComments() != null) {
                mWhat = MSG_HAVE_COMMENT;// have commnets.
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return question;
    }
}

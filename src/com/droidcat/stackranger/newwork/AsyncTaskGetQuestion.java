package com.droidcat.stackranger.newwork;

import java.io.IOException;

import net.sf.stackwrap4j.StackWrapper;
import net.sf.stackwrap4j.entities.Question;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.query.QuestionQuery;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.droidcat.stackranger.util.Config;

public class AsyncTaskGetQuestion extends AsyncTask<String, Integer, Question> {
    public static final String LOG_TAG = AsyncTaskGetQuestion.class
            .getSimpleName();
    private Handler mHandler;
    private String mUrl;
    private int mQuestionId;
    private int mWhat;
    private static final String FILTER_STRING = "!)65Ubs6S4uRBWEq-kouuN3hIFwl.";

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
    protected void onPostExecute(Question result) {
        Log.i(LOG_TAG, "List<Question> result = " + result);
        mHandler.handleMessage(mHandler.obtainMessage(mWhat, result));
    }

    @Override
    protected Question doInBackground(String... params) {
        StackWrapper stackWrapper = new StackWrapper(mUrl, Config.APIKEY);
        QuestionQuery qq = new QuestionQuery();
        qq.setFilter(FILTER_STRING);
        Log.i("error", qq.getUrlParams());
        mWhat = 0;// no comments yet~
        try {
            Question question = stackWrapper.getQuestionById(mQuestionId,
                    qq.getUrlParams());
            try {
                Log.i(LOG_TAG, question.getBody());
                Log.i(LOG_TAG, "getAnswers" + question.getAnswers());
                Log.i(LOG_TAG, "getComments" + question.getComments());
                if (question.getComments() != null) {
                    mWhat = 1;// have commnets.
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return question;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
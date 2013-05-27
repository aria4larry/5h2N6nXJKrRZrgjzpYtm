package com.droidcat.stackranger.newwork;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import com.droidcat.stackranger.util.Config;
import net.sf.stackwrap4j.StackWrapper;
import net.sf.stackwrap4j.entities.Question;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.query.QuestionQuery;

import java.io.IOException;
import java.util.List;

public class AsyncTaskGetQuestions extends
        AsyncTask<String, Integer, List<Question>> {

    private Handler mHandler;
    private String mSite;
    final static String FILTER_DEFAULT_ADD_VOTES = "!bULULQb52eMgYV";
    final QuestionQuery mQuestionQuery;
    public AsyncTaskGetQuestions(Handler handler, String site,QuestionQuery questionQuery) {
        super();
        mHandler = handler;
        mSite = site;
        mQuestionQuery = questionQuery;
    }

    @Override
    protected void onPostExecute(List<Question> result) {
        mHandler.handleMessage(mHandler.obtainMessage(0, result));
    }

    @Override
    protected List<Question> doInBackground(String... params) {
        StackWrapper stackWrapper = new StackWrapper(mSite, Config.APIKEY);
        mQuestionQuery.setFilter(FILTER_DEFAULT_ADD_VOTES);
        Log.i("error", mQuestionQuery.getUrlParams());
        try {
            return stackWrapper.listQuestions(mQuestionQuery);
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

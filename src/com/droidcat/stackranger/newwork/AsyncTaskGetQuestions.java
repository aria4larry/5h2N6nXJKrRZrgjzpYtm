package com.droidcat.stackranger.newwork;

import java.io.IOException;
import java.util.List;

import net.sf.stackwrap4j.StackWrapper;
import net.sf.stackwrap4j.entities.Question;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.query.QuestionQuery;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.droidcat.stackranger.util.Config;

public class AsyncTaskGetQuestions extends
        AsyncTask<String, Integer, List<Question>> {

    private Handler mHandler;
    private String mSite;

    public AsyncTaskGetQuestions(Context context, Handler handler, String site) {
        super();
        mHandler = handler;
        mSite = site;
    }

    @Override
    protected void onPostExecute(List<Question> result) {
        mHandler.handleMessage(mHandler.obtainMessage(0, result));
    }

    @Override
    protected List<Question> doInBackground(String... params) {
        StackWrapper stackWrapper = new StackWrapper(mSite, Config.APIKEY);
        QuestionQuery qq = new QuestionQuery();
        Log.i("error", qq.getUrlParams());
        try {
            return stackWrapper.listQuestions(qq);
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

package com.droidcat.stackranger.adapter;

import java.util.List;

import net.sf.stackwrap4j.entities.Question;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.droidcat.stackranger.newwork.AsyncTaskGetQuestions;

public class QuestionAdapter extends BaseAdapter {

    List<Question> mQuestions;
    Context mContext;
    Handler mHandler;
    public static final String LOGTAG = SitesAdapter.class.getSimpleName();

    @SuppressLint("HandlerLeak")
    public QuestionAdapter(Context context, String site) {
        mContext = context;
        mHandler = new Handler() {
            @SuppressWarnings("unchecked")
            @Override
            public void handleMessage(Message msg) {
                mQuestions = (List<Question>) msg.obj;
                if (mQuestions != null) {
                    Log.i(LOGTAG, "mQuestions  size" + mQuestions.size());
                }
                notifyDataSetChanged();
            }
        };
        new AsyncTaskGetQuestions(mContext, mHandler, site).execute();
    }

    @Override
    public int getCount() {
        return mQuestions == null ? 0 : mQuestions.size();
    }

    @Override
    public Object getItem(int location) {
        return mQuestions.get(location);
    }

    @Override
    public long getItemId(int location) {
        return mQuestions.get(location).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(mContext);
        textView.setText(mQuestions.get(position).getTitle());
        return textView;
    }

}

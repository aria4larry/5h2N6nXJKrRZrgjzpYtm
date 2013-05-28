package com.droidcat.stackranger.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.droidcat.stackranger.R;
import com.droidcat.stackranger.util.Utilis;
import net.sf.stackwrap4j.entities.Question;

import java.util.List;

public class QuestionsAdapter extends BaseAdapter {
    final static String LOGTAG = QuestionsAdapter.class.getSimpleName();
    static final int type_normal = 1;
    static final int type_progress = 2;
    List<Question> mQuestions;
    LayoutInflater mInflater;
    int mAnsweredColor;
    int mUnansweredColor;

    @SuppressLint("HandlerLeak")
    public QuestionsAdapter(Context context, LayoutInflater inflater) {
        mInflater = inflater;
        QuestionViewHolder.sContext = context;
        QuestionViewHolder.dp3px = Utilis.dip2px(context, 3f);
    }

    public void setData(List<Question> questions) {
        mQuestions = questions;
    }

    public void setBgandFg(int bg, int fg) {
        QuestionViewHolder.sbg = bg;
        QuestionViewHolder.sfg = fg;
    }

    @Override
    public int getCount() {
        return mQuestions == null ? 0 : mQuestions.size() + 1;
    }

    @Override
    public Object getItem(int location) {
        return mQuestions.get(location);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getCount() - 1) {
            return type_progress;
        } else {
            return type_normal;
        }
    }

    @Override
    public long getItemId(int location) {
        return mQuestions.get(location).hashCode();
    }
    View mProgressing = null ;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (getItemViewType(position)) {
            case type_progress:
                if (mProgressing == null){
                    mProgressing = mInflater.inflate(R.layout.footer_loading_view,null);
                }
                return mProgressing;
            case type_normal:
            default:
                QuestionViewHolder holder = QuestionViewHolder.createOrRecycle(mInflater, convertView, mQuestions.get(position));
                return holder.mRootView;
        }

    }

    public void setColors(int answered, int unanswered) {
        mAnsweredColor = answered;
        mUnansweredColor = unanswered;
        QuestionViewHolder.sAnsweredColor = mAnsweredColor;
        QuestionViewHolder.sUnansweredColor = mUnansweredColor;
    }

    static class QuestionViewHolder {
        static int sbg;
        static int sfg;
        static Context sContext;
        static int dp3px;
        static int sAnsweredColor;
        static int sUnansweredColor;
        static int sColorAnsweredAccepted;

        static {
            sColorAnsweredAccepted = Color.parseColor("#E1E818");
        }

        Question mQuestion;
        TextView mVotesN;
        TextView mAnswersN;
        TextView mAnswersL;
        TextView mViewsN;
        TextView mTitle;
        LinearLayout mTags;
        TextView mTag;
        View mRootView;

        public static QuestionViewHolder createOrRecycle(LayoutInflater inflater,
                                                         View convertView, Question question) {
            QuestionViewHolder holder;
            if (convertView == null || convertView.getTag()==null) {
                convertView = inflater.inflate(R.layout.question_list_item, null);
                holder = new QuestionViewHolder();
                holder.mRootView = convertView;
                holder.mVotesN = (TextView) convertView
                        .findViewById(R.id.q_vote);
                holder.mAnswersN = (TextView) convertView
                        .findViewById(R.id.answerN);
                holder.mAnswersL = (TextView) convertView
                        .findViewById(R.id.answersL);
                holder.mViewsN = (TextView) convertView
                        .findViewById(R.id.viewsN);
                holder.mTitle = (TextView) convertView
                        .findViewById(R.id.question_title);
                holder.mTags = (LinearLayout) convertView.findViewById(R.id.tags);
                holder.mTag = (TextView) convertView.findViewById(R.id.tag);
                convertView.setTag(holder);
            } else {
                holder = (QuestionViewHolder) convertView.getTag();
            }
            holder.mQuestion = question;
            holder.updateView();
            return holder;
        }

        private void updateView() {
            mTag.setBackgroundColor(sbg);
            mTag.setTextColor(sfg);
            mVotesN.setText(Integer.toString(mQuestion.getUp_vote_count() - mQuestion.getDown_vote_count()));
            mAnswersN.setText(Integer.toString(mQuestion.getAnswer_count()));
            if (mQuestion.getAnswer_count() > 0) {
                mAnswersL.setBackgroundColor(sAnsweredColor);
                mAnswersN.setBackgroundColor(sAnsweredColor);
                if (mQuestion.getAccepted_answer_id() != -1) {
                    mAnswersL.setTextColor(sColorAnsweredAccepted);
                    mAnswersN.setTextColor(sColorAnsweredAccepted);
                } else {
                    mAnswersL.setTextColor(Color.WHITE);
                    mAnswersN.setTextColor(Color.WHITE);
                }
            } else {
                mAnswersL.setBackgroundColor(Color.TRANSPARENT);
                mAnswersN.setBackgroundColor(Color.TRANSPARENT);
                mAnswersL.setTextColor(sUnansweredColor);
                mAnswersN.setTextColor(sUnansweredColor);
            }
            mViewsN.setText(Integer.toString(mQuestion.getView_count()));
            mTitle.setText(mQuestion.getTitle());
            updateTags(mQuestion.getTags());
        }

        private void updateTags(List<String> tags) {
            int childCount = mTags.getChildCount();
            if (tags == null) {
                mTags.setVisibility(View.GONE);
            } else {
                mTags.setVisibility(View.VISIBLE);
                int i = 0;
                for (; i < tags.size(); i++) {
                    TextView textView;
                    if (i < childCount) {//use the old one
                        textView = (TextView) mTags.getChildAt(i);
                    } else {
                        textView = new TextView(sContext);
                        textView.setBackgroundColor(sbg);
                        textView.setTextColor(sfg);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                        params.setMargins(dp3px, 0, 0, 0);
                        textView.setLayoutParams(params);
                        textView.setPadding(dp3px, dp3px, dp3px, dp3px);
                        mTags.addView(textView);
                    }
                    textView.setText(tags.get(i));
                    mTags.getChildAt(i).setVisibility(View.VISIBLE);
                }
                for (; i < childCount; i++) {
                    mTags.getChildAt(i).setVisibility(View.GONE);
                }
            }
        }
    }

}

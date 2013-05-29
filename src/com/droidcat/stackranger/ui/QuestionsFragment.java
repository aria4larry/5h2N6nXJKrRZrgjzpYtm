package com.droidcat.stackranger.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.droidcat.stackranger.R;
import com.droidcat.stackranger.adapter.QuestionsAdapter;
import com.droidcat.stackranger.newwork.AsyncTaskGetQuestions;
import com.droidcat.stackranger.util.Utilis;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import net.sf.stackwrap4j.entities.Question;
import net.sf.stackwrap4j.query.QuestionQuery;

import java.util.ArrayList;
import java.util.List;

public class QuestionsFragment extends ListFragment
        implements PullToRefreshBase.OnRefreshListener, PullToRefreshBase.OnLastItemVisibleListener {

    public static final String TAG_BACKGROUND = "TAG_BG";
    public static final String TAG_FOREGROUND = "TAG_FG";
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_SITE = "SITE";
    static final String LOG_TAG = QuestionsFragment.class.getSimpleName();
    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onQuestionSelected(Question question) {
            // TODO Auto-generated method stub
        }
    };
    String mSite = null;
    Handler mHandler;
    boolean mIsLoading = false;
    private QuestionsAdapter mQuestionAdapter;
    private Callbacks mCallbacks;
    private PullToRefreshListView mPullToRefreshListView;
    private QuestionQuery mQuestionQuery;
    private List<Question> mQuestions;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public QuestionsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mQuestions = new ArrayList<Question>();
        if (bundle != null && bundle.containsKey(ARG_SITE)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mSite = getArguments().getString(ARG_SITE);
            mHandler = new Handler() {
                @SuppressWarnings("unchecked")
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case AsyncTaskGetQuestions.MSG_LOADING:
                            mIsLoading = true;
                            break;
                        case AsyncTaskGetQuestions.MSG_LOADING_COMPLETE:
                            mIsLoading = false;
                        default:
                            List<Question> questions = (List<Question>) msg.obj;
                            if (questions != null && questions.size() > 0) {
                                mQuestions.addAll(questions);
                                mQuestionAdapter.setData(mQuestions);
                                mQuestionAdapter.notifyDataSetChanged();
                            } else {
                                Utilis.showToast(getActivity(), R.string.toast_no_data_fenched, Toast.LENGTH_LONG);
                            }
                            mPullToRefreshListView.onRefreshComplete();
                    }
                }
            };
            mQuestionQuery = new QuestionQuery();
            mQuestionQuery.setPage(1);
            mQuestionQuery.setPageSize(25);
            mQuestionAdapter = new QuestionsAdapter(getActivity(), getActivity().getLayoutInflater());
            mQuestionAdapter.setBgandFg(bundle.getInt(TAG_BACKGROUND), bundle.getInt(TAG_FOREGROUND));
            setListAdapter(mQuestionAdapter);
        }
    }

    private void refresh() {
        mQuestionQuery.restoreDefaults();
        mQuestions.clear();
        new AsyncTaskGetQuestions(mHandler, mSite, mQuestionQuery).execute();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException(
                    "Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.questions_list, null);
        ListView lv = (ListView) rootView.findViewById(android.R.id.list);
        ViewGroup parent = (ViewGroup) lv.getParent();
        int lvIndex = parent.indexOfChild(lv);
        parent.removeViewAt(lvIndex);
        mPullToRefreshListView = new PullToRefreshListView(getActivity());
        mPullToRefreshListView.setOnRefreshListener(this);
        mPullToRefreshListView.setOnLastItemVisibleListener(this);
        parent.addView(mPullToRefreshListView, lvIndex, lv.getLayoutParams());
        mPullToRefreshListView.setScrollEmptyView(true);
        mQuestionAdapter.setColors(getResources().getColor(R.color.color_has_ans),
                getResources().getColor(R.color.color_0ans));
        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        position -= 1;//because the pull2refresh added a view?TODO:
        super.onListItemClick(l, v, position, id);
        mCallbacks.onQuestionSelected((Question) mQuestionAdapter
                .getItem(position));
    }

    void loadMore() {
        mQuestionQuery.incrementPage();
        Log.i(LOG_TAG, "loadMore" + mQuestionQuery.getPage());
        new AsyncTaskGetQuestions(mHandler, mSite, mQuestionQuery).execute();
    }

    @Override
    public void onLastItemVisible() {
        if (!mIsLoading) {

            Log.i(LOG_TAG, "onLastItemVisible");
            loadMore();
        }

    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (!mIsLoading) {

            refresh();
        }

    }

    @SuppressLint("NewApi")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        new AsyncTaskGetQuestions(mHandler, mSite, mQuestionQuery).execute();

    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onQuestionSelected(Question question);
    }

}

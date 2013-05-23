package com.droidcat.stackranger.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.droidcat.stackranger.adapter.QuestionAdapter;
import net.sf.stackwrap4j.entities.Question;

public class QuestionsFragment extends ListFragment {

    public static final String TAG_BACKGROUND = "TAG_BG";
    public static final String TAG_FOREGROUND = "TAG_FG";
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_SITE = "SITE";
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
    private QuestionAdapter mQuestionAdapter;
    private Callbacks mCallbacks;

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
        if (bundle != null && bundle.containsKey(ARG_SITE)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            String site = getArguments().getString(ARG_SITE);
            mQuestionAdapter = new QuestionAdapter(getActivity(),getActivity().getLayoutInflater(), site);
            mQuestionAdapter.setBgandFg(bundle.getInt(TAG_BACKGROUND), bundle.getInt(TAG_FOREGROUND));
            setListAdapter(mQuestionAdapter);
        }
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
        View rootView = inflater.inflate(android.R.layout.list_content,
                container, false);

        // Show the dummy content as text in a TextView.

        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        mCallbacks.onQuestionSelected((Question) mQuestionAdapter
                .getItem(position));
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

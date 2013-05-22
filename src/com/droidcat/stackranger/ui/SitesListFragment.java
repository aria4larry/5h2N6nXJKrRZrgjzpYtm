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
import com.droidcat.stackranger.adapter.SitesAdapter;
import com.droidcat.stackranger.cache.SitesCache;
import com.droidcat.stackranger.newwork.AsyncTaskGetSites;
import com.droidcat.stackranger.util.Utilis;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import net.sf.stackwrap4j.query.BaseQuery;
import net.sf.stackwrap4j.query.PageQuery;
import net.sf.stackwrap4j.stackauth.entities.Site;

import java.util.ArrayList;
import java.util.List;

/**
 * A list fragment representing a list of Items. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link QuestionsFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class SitesListFragment extends ListFragment implements
        PullToRefreshBase.OnRefreshListener<ListView> {

    public static final String MODE_FROM_CACHE = "MODE_FROM_CACHE";
    public static final String MODE_FROM_NET = "MODE_FROM_NET";
    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private static final String LOG_TAG = SitesListFragment.class.getSimpleName();
    private static final int page = 1;
    private static final int pageSize = 25;
    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onSiteSelected(Site site) {
            // TODO Auto-generated method stub
        }
    };
    Handler mHandler;
    private SitesAdapter mSitesAdapter;
    private PullToRefreshListView mPullToRefreshListView;
    private PageQuery mPageQuery;
    private List<Site> mSites;
    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks;
    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * Mandatory empjava.lang.Stringty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SitesListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.sites_list, null);
        ListView lv = (ListView) layout.findViewById(android.R.id.list);
        ViewGroup parent = (ViewGroup) lv.getParent();

        // Remove ListView and add PullToRefreshListView in its place
        int lvIndex = parent.indexOfChild(lv);
        parent.removeViewAt(lvIndex);
        mPullToRefreshListView = new PullToRefreshListView(getActivity());
        mPullToRefreshListView.setOnRefreshListener(this);
        parent.addView(mPullToRefreshListView, lvIndex, lv.getLayoutParams());
        mPullToRefreshListView.setScrollEmptyView(true);
        return layout;
    }

    private void refresh() {
        //TODO:clear the cached sites when refreshing...
        mPageQuery.restoreDefaults();
        mSites.clear();
        try {
            SitesCache.getInstance().cleanSitesCache();
        } catch (Exception e) {
            Log.w(LOG_TAG, e.getMessage());
        }
        new AsyncTaskGetSites(mHandler, mPageQuery).execute(MODE_FROM_NET);
    }

    private void loadMore() {
        mPageQuery.incrementPage();
        new AsyncTaskGetSites(mHandler, mPageQuery).execute(MODE_FROM_NET);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSites = new ArrayList<Site>();
        mSitesAdapter = new SitesAdapter(getActivity().getLayoutInflater());
        mHandler = new Handler() {
            @SuppressWarnings("unchecked")
            @Override
            public void handleMessage(Message msg) {
                List<Site> sites = (List<Site>) msg.obj;
                if (sites != null && sites.size() > 0) {
                    Log.i(LOG_TAG, "sites  size" + sites.size());
                    mSites.addAll(sites);
                    mSitesAdapter.setData(mSites);
                    mSitesAdapter.notifyDataSetChanged();
                    mPullToRefreshListView.onRefreshComplete();
                } else {
                    Utilis.showToast(getActivity(), R.string.toast_no_data_fenched, Toast.LENGTH_LONG);
                }

            }
        };
        mPageQuery = new

                PageQuery(null) {
                    @Override
                    public BaseQuery restoreDefaults() {
                        put("page", page + "");
                        put("pagesize", pageSize + "");
                        return this;
                    }
                };
        mPageQuery.setPage(1);
        //TODO: add this to settings page...
        mPageQuery.setPageSize(pageSize);
        setListAdapter(mSitesAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState
                    .getInt(STATE_ACTIVATED_POSITION));
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

    @SuppressLint("NewApi")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        new AsyncTaskGetSites(mHandler, mPageQuery).execute(MODE_FROM_CACHE);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position,
                                long id) {
        position -= 1;//because the pull2refresh added a view?TODO:
        Log.i(LOG_TAG, "onListItemClick...position=" + position + " id=" + id);
        super.onListItemClick(listView, view, position, id);
        setActivatedPosition(position);
        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        if (position < mSitesAdapter.getCount() - 1) {
            mCallbacks.onSiteSelected((Site) mSitesAdapter.getItem(position));
        } else {
            loadMore();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    @SuppressLint("NewApi")
    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
            mActivatedPosition = position;

        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        refresh();
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
        public void onSiteSelected(Site site);
    }
}

package com.droidcat.stackranger.newwork;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import com.droidcat.stackranger.cache.SitesCache;
import com.droidcat.stackranger.ui.SitesListFragment;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.query.PageQuery;
import net.sf.stackwrap4j.stackauth.StackAuth;
import net.sf.stackwrap4j.stackauth.entities.Site;

import java.io.IOException;
import java.util.List;

public class AsyncTaskGetSites extends AsyncTask<String, Integer, List<Site>> {
    private static final String TAG = AsyncTaskGetSites.class.getSimpleName();
    private Handler mHandler;
    private PageQuery mPageQuery;

    public AsyncTaskGetSites(Handler handler, PageQuery pageQuery) {
        super();
        mHandler = handler;
        mPageQuery = pageQuery;
    }

    @Override
    protected void onPostExecute(List<Site> result) {
        mHandler.handleMessage(mHandler.obtainMessage(0, result));
    }

    @Override
    protected List<Site> doInBackground(String... params) {
        Log.i(TAG, "params, " + params[0].toString());
        try {
            if (params[0].equals(SitesListFragment.MODE_FROM_CACHE)) {
                List<Site> sites;
                if ((sites = SitesCache.getInstance().getCachedSites()) != null) {
                    return sites;
                }
            }
            return StackAuth.getAllSites(mPageQuery);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

package com.droidcat.stackranger.cache;

import android.content.Context;
import android.util.Log;

import com.droidcat.stackranger.util.FileUtils;

import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.json.JSONObject;
import net.sf.stackwrap4j.stackauth.entities.Site;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mtk54039 on 13-5-16.
 */
public class SitesCache {
    private static final String TAG = SitesCache.class.getSimpleName();
    private static Context sContext;
    private static SitesCache sSitesCache;

    private SitesCache() {

    }

    public static void setContext(Context pContext) {
        sContext = pContext;
    }

    public static SitesCache getInstance() throws Exception {
        if (sContext == null) {
            throw new Exception("must set context before use... setContext(context)");
        }
        if (sSitesCache == null) {
            sSitesCache = new SitesCache();
        }
        return sSitesCache;
    }

    public boolean cacheSite(Site pSite, JSONObject data) {
        try {
            FileUtils.writeStringCache(sContext, Integer.toString(pSite.getSite_url().hashCode()), data.toString(), TAG);
            return true;
        } catch (Exception ex) {
            Log.w(TAG, "failed to cache site " + pSite.getName());
        }
        return false;
    }

    public void cleanSitesCache(){
        FileUtils.deleteSitesCache(sContext,TAG);
    }

    public List<Site> getCachedSites() {
        Log.i(TAG, "getCachedSites----");
        List<Site> sites = new ArrayList<Site>();
        List<String> sitesString = FileUtils.readStringCaches(sContext, TAG);
        if (sitesString == null) {
            return null;
        }
        for (String siteString : sitesString) {
            try {
                sites.add(new Site(new JSONObject(siteString)));
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return sites;
    }

}

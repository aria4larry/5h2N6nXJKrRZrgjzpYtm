package com.droidcat.stackranger.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.droidcat.stackranger.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import net.sf.stackwrap4j.stackauth.entities.Site;

import java.util.List;

public class SitesAdapter extends BaseAdapter {
    public static final String LOG_TAG = SitesAdapter.class.getSimpleName();
    static final int TYPE_NORMAL = 0;
    static final int TYPE_CLICK2LOAD = 1;
    List<Site> mSites;
    LayoutInflater mInflater;
    View loadMoreView;

    public SitesAdapter(LayoutInflater layoutInflater) {
        mInflater = layoutInflater;
    }

    public void setData(List<Site> pSites) {
        mSites = pSites;
        for (Site site : mSites) {
            Log.i(LOG_TAG, "site---" + site.toString());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mSites.size()) {
            return TYPE_CLICK2LOAD;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public int getCount() {
        //+1 for showing the click to load more item...
        return mSites == null ? 0 : mSites.size() + 1;
    }

    @Override
    public Object getItem(int location) {
        Log.i(LOG_TAG, "getItem = " + location);
        if (location == mSites.size()) {
            Log.i(LOG_TAG, "getItem = return null" + location);
            return null;
        }
        return mSites.get(location);
    }

    @Override
    public long getItemId(int location) {
        Log.i(LOG_TAG, "getItemId = " + location);
        if (location == mSites.size()) {
            Log.i(LOG_TAG, "getItemId = return null" + location);
            return -1;
        }
        return (mSites.get(location).getSite_url()).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(LOG_TAG, "getView = " + position);
        switch (getItemViewType(position)) {
            case TYPE_CLICK2LOAD:
                Log.i(LOG_TAG, "getView = clickto add view``~" + position);
                if (loadMoreView == null) {
                    loadMoreView = mInflater.inflate(R.layout.click_to_load_more, null);
                }
                return loadMoreView;
            case TYPE_NORMAL:
            default:
                SiteViewHolder holder = SiteViewHolder.createOrRecycle(mInflater,
                        convertView,mSites.get(position));
                return holder.mRootView;

        }

    }

    static class SiteViewHolder {
        ImageView mSiteIcon;
        TextView mName;
        TextView mDes;
        Site mSite;
        View mRootView;

        public static SiteViewHolder createOrRecycle(LayoutInflater inflater,
                                                     View convertView,Site site) {
            SiteViewHolder holder;
            if (convertView == null || convertView.getTag() == null) {
                //convertView.getTag()==null means the view is the click2loadview
                convertView = inflater.inflate(R.layout.site_list_item, null);
                holder = new SiteViewHolder();
                holder.mRootView = convertView;
                holder.mDes = (TextView) convertView
                        .findViewById(R.id.site_des);
                holder.mName = (TextView) convertView
                        .findViewById(R.id.site_name);
                holder.mSiteIcon = (ImageView) convertView
                        .findViewById(R.id.site_icon);
                convertView.setTag(holder);
            } else {
                holder = (SiteViewHolder) convertView.getTag();
            }
            holder.mSite = site;
            holder.updateView();
            return holder;
        }
        void updateView(){
            mDes.setText(mSite.getAudience());
            mName.setText(mSite.getName());
            ImageLoader.getInstance().displayImage(mSite.getIcon_url(),
                    mSiteIcon);
            mRootView.setBackgroundColor(mSite.getStyling().getTagBackgroundColor());
            mName.setTextColor(mSite.getStyling().getLinkColor());
        }
    }

}

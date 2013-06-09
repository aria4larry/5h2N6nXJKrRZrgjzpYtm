package com.droidcat.stackranger.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.droidcat.stackranger.R;
import com.droidcat.stackranger.cache.SitesCache;
import com.droidcat.stackranger.util.Utilis;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import net.sf.stackwrap4j.entities.Question;
import net.sf.stackwrap4j.stackauth.entities.Site;

/**
 * An activity representing a list of Items. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link QuestionActivity} representing item details. On tablets, the activity
 * presents the list of items and item details side-by-side using two vertical
 * panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link SitesListFragment} and the item details (if present) is a
 * {@link QuestionsFragment}.
 * <p/>
 * This activity also implements the required
 * {@link SitesListFragment.Callbacks} interface to listen for item selections.
 */
public class MainActivity extends SlidingFragmentActivity implements
        SitesListFragment.Callbacks, QuestionsFragment.Callbacks, View.OnClickListener
        , GetOauthFragment.OauthCallBack {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    DisplayImageOptions defaultDisplayImageOptions;
    SlidingMenu sm;
    QuestionsFragment questionsFragment;
    TextView mTitle;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private String mEndPoint = "stackoverflow";
    private ImageView mBtnSites;
    private ProgressBar mProgressBar;
    private RelativeLayout mRefresh;
    private ImageView mRefreshView;
    private SharedPreferences mActivityPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SitesCache.setContext(getApplicationContext());
        defaultDisplayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory().cacheOnDisc()
                .displayer(new RoundedBitmapDisplayer(5))
                .displayer(new FadeInBitmapDisplayer(500)).build();
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(
                this).defaultDisplayImageOptions(
                defaultDisplayImageOptions).build();
        ImageLoader.getInstance().init(configuration);
        setBehindContentView(R.layout.menu_frame);
        setContentView(R.layout.activity_main_single);
        questionsFragment = new QuestionsFragment();
        questionsFragment.setArgSite("stackoverflow");
        questionsFragment.setBgandFg(Color.parseColor("#E0EAF1"), Color.parseColor("#3E6D8E"));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.questions, questionsFragment).commit();
        if (findViewById(R.id.question) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.question, new QuestionFragment()).commit();
            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
        }
        mBtnSites = (ImageView) findViewById(R.id.btn_category);
        mBtnSites.setOnClickListener(this);
        mProgressBar = (ProgressBar) findViewById(R.id.top_progress);
        mRefresh = (RelativeLayout) findViewById(R.id.title_click_layout);
        mRefresh.setOnClickListener(this);
        mRefreshView = (ImageView) findViewById(R.id.top_refresh);
        mTitle = (TextView) findViewById(R.id.textTitle);
        setupSlidingMenu();
        // TODO: If exposing deep links into your app, handle intents here.
    }

    private boolean isOauthed() {
        mActivityPref = getPreferences(MODE_PRIVATE);
        String access_token = mActivityPref.getString("access_token", null);
        if (access_token == null || access_token.equals("")) {//not authed
            return false;
        } else {
            Utilis.access_token = access_token;
            return true;
        }
    }

    private void setupSlidingMenu() {
        sm = getSlidingMenu();
        sm.setMode(SlidingMenu.LEFT_RIGHT);
        //TODO:add right sliding menu for user account infomation
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setShadowDrawable(R.drawable.shadow);
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sm.setFadeDegree(0.35f);
        sm.setSecondaryMenu(R.layout.menu_frame);
        sm.setSecondaryShadowDrawable(R.drawable.shadow);
        SitesListFragment sitesListFragment = new SitesListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.menu_frame, sitesListFragment).commit();
        sm.setSecondaryMenu(R.layout.menu_frame_two);
        sm.setSecondaryShadowDrawable(R.drawable.shadowright);
        if (isOauthed()) {
            UserCenterFragment lUserCenterFragment = new UserCenterFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_two, lUserCenterFragment)
                    .commit();
        } else {
            GetOauthFragment lGetOauthFragment = new GetOauthFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_two, lGetOauthFragment)
                    .commit();
        }
    }

    /**
     * Callback method from {@link SitesListFragment.Callbacks} indicating that
     * the item with the given ID was selected.
     */
    @Override
    public void onSiteSelected(Site site) {
        // if (mTwoPane) {
        // In two-pane mode, show the detail view in this activity by
        // adding or replacing the detail fragment using a
        // fragment transaction.
        setTitle(site.getName());
        mEndPoint = site.getApi_site_parameter();
        questionsFragment.setArgSite(mEndPoint);
        questionsFragment.setBgandFg(site.getStyling().getTagBackgroundColor(), site.getStyling().getTagForegroundColor());
        questionsFragment.refresh();
        sm.toggle();
        if (mTwoPane) {
            //TODO: update the two panel mode.
            Log.i(LOG_TAG, "function not available yet TODO...");
        }
    }

    @Override
    public void onQuestionSelected(Question question) {
        if (mTwoPane) {
            // TODO: update the question view using fragment.
            Log.i(LOG_TAG, "function not available yet TODO...");
        } else {
            Intent questionIntent = new Intent(this, QuestionActivity.class);
            questionIntent.putExtra(QuestionFragment.KEY_QUESTION_ID,
                    question.getPostId());
            questionIntent.putExtra(QuestionsFragment.ARG_SITE, mEndPoint);
            startActivity(questionIntent);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle.setText(title);
    }

    @Override
    public void onLoadingStatusChanged(boolean loading) {
        if (loading) {
            mRefreshView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mRefreshView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_category:
                sm.showMenu();
                break;
            case R.id.title_click_layout:
                questionsFragment.refresh();
                break;
        }
    }

    @Override
    public void OnSuccess(String access_token) {
        //TODO:
        Utilis.access_token = access_token;
        Utilis.showToast(this, "loging successed, access_token = " + access_token, Toast.LENGTH_LONG);
    }
}

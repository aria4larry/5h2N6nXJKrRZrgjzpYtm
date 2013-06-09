package com.droidcat.stackranger.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.droidcat.stackranger.R;

/**
 * Created by mtk54039 on 13-6-9.
 */
public class GetOauthFragment extends Fragment {
    //https://stackexchange.com/oauth/dialog?client_id=1522&scope=private_info,no_expiry,write_access ,read_inbox&redirect_uri=https://stackexchange.com/oauth/login_success
    private static final String SERVER = "https://stackexchange.com/oauth/dialog";
    private static final String CLIENT_ID = "1522";
    private static final String SCOPE_PRIVATE_INFO = "private_info";
    private static final String SCOPE_NO_EXPIRY = "no_expiry";
    private static final String SCOPE_WRITE_ACCESS = "write_access";
    private static final String SCOPE_READ_INBOX = "read_inbox";
    private static final String REDIRECT_URI = "https://stackexchange.com/oauth/login_success";
    private static String mUrl;
    private  SharedPreferences mPref;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WebView lWebView = (WebView) getActivity().findViewById(R.id.oauth_webView);
        lWebView.setWebViewClient(new SampleWebViewClient());
        WebSettings lWebSettings = lWebView.getSettings();
        lWebSettings.setJavaScriptEnabled(true);
//        lWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        lWebView.loadUrl(mUrl);
    }

    public GetOauthFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.oauth, null);
    }

    static {
        mUrl = SERVER + "?client_id=" + CLIENT_ID + "&scope=" + SCOPE_NO_EXPIRY + "," + SCOPE_PRIVATE_INFO + "," + SCOPE_READ_INBOX
                + "," + SCOPE_WRITE_ACCESS + "&redirect_uri=" + REDIRECT_URI;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private class SampleWebViewClient extends WebViewClient {
        private String LOG_TAG=SampleWebViewClient.class.getSimpleName();

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            if (url.contains("access_token")) {
                String [] paras = url.split("=");
                String access_token = paras[paras.length-1];
                Log.i(LOG_TAG,"access_token"+access_token+" url="+url);
                SharedPreferences.Editor lEditor = mPref.edit();
                lEditor.putString("access_token", access_token);
                lEditor.commit();
                ((OauthCallBack)getActivity()).OnSuccess(access_token);
            }
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    }

    public interface OauthCallBack{
        public void OnSuccess(String access_token);
    }
}
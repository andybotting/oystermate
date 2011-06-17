package com.andybotting.oystermate.activity;

import com.andybotting.oystermate.R;
import com.andybotting.oystermate.utils.PreferenceHelper;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class WebLaunch extends Activity {  
	
	public static final String INTENT_URL = "intent_url";
	
	WebView mWebView;
	String mUrl;
  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
    	super.onCreate(savedInstanceState);  
    	setContentView(R.layout.webview);  

    	// Get the URL
		mUrl = getIntent().getExtras().getString(INTENT_URL);
		
    	mWebView = (WebView) findViewById(R.id.webview);
    	new WebViewTask().execute();  
    	
        mWebView.setWebChromeClient(new WebChromeClient() {
        	public void onProgressChanged(WebView view, int progress) {
        		loadingStatus(progress < 100 ? true : false);
        	}
        });
    	
    }  
  
    
    /**
     * Update refresh status spinner
     */
	private void loadingStatus(boolean isLoading) {
		findViewById(R.id.title_refresh_progress).setVisibility(isLoading ? View.VISIBLE : View.GONE);
	}
    
    
    /**
     * WebView which handles cookie sync in the background
     * @author andy
     *
     */
    private class WebViewTask extends AsyncTask<Void, Void, Boolean> {  
        String sessionCookie;  
        CookieManager cookieManager;  
  
        @Override  
        protected void onPreExecute() {  
        	
        	loadingStatus(true);
        	
        	PreferenceHelper preferenceHelper = new PreferenceHelper();
        	String sessionId = preferenceHelper.getSessionId();
        	
        	CookieSyncManager.createInstance(WebLaunch.this);  
        	cookieManager = CookieManager.getInstance();
        	sessionCookie = "JSESSIONID=" + sessionId + "; domain=oyster.tfl.gov.uk";
        	
        	if (sessionCookie != null)  
        		cookieManager.removeSessionCookie();  
            super.onPreExecute();  
        }  
        
        protected Boolean doInBackground(Void... param) {  
        	// this is very important - THIS IS THE HACK  
        	SystemClock.sleep(1000);  
        	return false;  
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
        	
            if (sessionCookie != null) {  
                cookieManager.setCookie(OysterDetails.TFL_URL + "/", sessionCookie);  
                CookieSyncManager.getInstance().sync();  
            }  
            
            WebSettings webSettings = mWebView.getSettings();  
            webSettings.setJavaScriptEnabled(true);  
            webSettings.setBuiltInZoomControls(true);  
            mWebView.setWebViewClient(new WebViewClient() {  
            	@Override  
            	public boolean shouldOverrideUrlLoading(WebView view, String url) {  
            		return super.shouldOverrideUrlLoading(view, url);  
            	}  
            });  

            mWebView.loadUrl(mUrl);  
        }  
    }  
}
package com.andybotting.oystermate.activity;

import com.andybotting.oystermate.R;
import com.andybotting.oystermate.utils.PreferenceHelper;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;

public class WebLaunch extends SherlockActivity {

	public static final String INTENT_URL = "intent_url";

	private WebView mWebView;
	private String mUrl;

	final SherlockActivity MyActivity = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.webview);
		
		// Set up the Action Bar
		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Get the URL
		mUrl = getIntent().getExtras().getString(INTENT_URL);

		mWebView = (WebView) findViewById(R.id.webview);
		new WebViewTask().execute();

		mWebView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				MyActivity.setSupportProgress(progress * 100);
	            setSupportProgressBarVisibility(true);

	            if (progress == 100) {
	                setSupportProgressBarVisibility(false);
	            }

			}
		});
	}

	/**
	 * WebView which handles cookie sync in the background
	 */
	private class WebViewTask extends AsyncTask<Void, Void, Boolean> {
		String sessionCookie;
		CookieManager cookieManager;

		@Override
		protected void onPreExecute() {

			//loadingStatus(true);

			PreferenceHelper preferenceHelper = new PreferenceHelper();
			String sessionId = preferenceHelper.getSessionId();

			CookieSyncManager.createInstance(WebLaunch.this);
			cookieManager = CookieManager.getInstance();
			sessionCookie = "JSESSIONID=" + sessionId + "; domain=oyster.tfl.gov.uk";

			if (sessionCookie != null) cookieManager.removeSessionCookie();
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
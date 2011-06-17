package com.andybotting.oystermate.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.andybotting.oystermate.utils.PreferenceHelper;

import android.util.Log;

public class HttpConnection {
	
    private static final String TAG = "OysterMate";
    private static final boolean LOGV = Log.isLoggable(TAG, Log.INFO);

	private static final String USER_AGENT = "UserAgent";
	private static final String LOGIN_URL = "https://oyster.tfl.gov.uk/oyster/security_check";
	
	/**
	 * 
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws OysterProviderException
	 */
	public String fetchDocument(String username, String password) throws IOException {
	
		if (LOGV) Log.i(TAG, "Fetching content for " + username);
		String output = null;
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
	    HttpPost httpPost = new HttpPost(LOGIN_URL);
	    httpPost.setHeader("User-Agent", USER_AGENT);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("j_username", username));
        nameValuePairs.add(new BasicNameValuePair("j_password", password));
        
		httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		HttpResponse response = httpClient.execute(httpPost);
		
		if (LOGV) Log.i(TAG, response.getAllHeaders().toString());
		
		HttpEntity entity = response.getEntity();
		output = convertStreamToString(entity.getContent());	
	
		List<Cookie> cookies = httpClient.getCookieStore().getCookies();
		PreferenceHelper preferenceHelper = new PreferenceHelper();
		
		if (!cookies.isEmpty()) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().matches("JSESSIONID"))
					preferenceHelper.setSessionId(cookie.getValue());
			}
		}
		
		return output;
		
	}

	
	
	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine()
		 * method. We iterate until the BufferedReader return null which means
		 * there's no more data to read. Each line will appended to a StringBuilder
		 * and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
				//sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}	

}

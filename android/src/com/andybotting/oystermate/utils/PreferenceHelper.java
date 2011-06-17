package com.andybotting.oystermate.utils;

import com.andybotting.oystermate.OysterMate;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceHelper {
	
	private static final String KEY_USERNAME = "username";
	private static final String KEY_PASSWORD = "password";
	private static final String KEY_SESSION_ID = "session_id";
	
	private final SharedPreferences preferences;
	
	public PreferenceHelper() {
		Context context = OysterMate.getContext();
		this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	
	/***
	 * Check to see if we have any credentials saved
	 * @return
	 */
	public boolean hasCredentials() {
		if ( (getUsername() != null) && (getPassword() != null) )
			return true;
		return false;
			
	}
	
    /**
     * Return the username
     */
	public String getUsername() {
		return preferences.getString(KEY_USERNAME, null);
	}
	
    /**
     * Return the password
     */
	public String getPassword() {
		return preferences.getString(KEY_PASSWORD, null);
	}
	
	
    /**
     * Return the cookie
     */
	public String getSessionId() {
		return preferences.getString(KEY_SESSION_ID, null);
	}
	

    /**
     * Set the Session ID
     */	
	public void setSessionId(String sessionId) {
		SharedPreferences.Editor editor = preferences.edit();	
		editor.putString(KEY_SESSION_ID, sessionId);
		editor.commit();
	}	
	
	
	/**
	 * Clear the credentials for logout
	 */
	public void clearCredentials() {
		SharedPreferences.Editor editor = preferences.edit();	
		editor.putString(KEY_USERNAME, null);
		editor.putString(KEY_PASSWORD, null);
		editor.commit();		
	}
	
    /**
     * Set a string representing the GUID
     */	
	public void setCredentials(String username, String password) {
		SharedPreferences.Editor editor = preferences.edit();	
		editor.putString(KEY_USERNAME, username);
		editor.putString(KEY_PASSWORD, password);
		editor.commit();
	}	
	
}

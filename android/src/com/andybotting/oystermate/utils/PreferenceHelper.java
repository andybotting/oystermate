package com.andybotting.oystermate.utils;

import java.util.Date;

import com.andybotting.oystermate.OysterMate;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceHelper {

	private static final String KEY_USERNAME = "username";
	private static final String KEY_PASSWORD = "password";
	private static final String KEY_LAST_VIEWED_CARD = "last_viewed_card";
	private static final String KEY_SESSION_ID = "session_id";
	private static final String KEY_SESSION_TIMESTAMP = "stats_timestamp";

	private static final int SESSION_LIFETIME_MINUTES = 10;

	private final SharedPreferences preferences;

	public PreferenceHelper() {
		Context context = OysterMate.getContext();
		this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	/***
	 * Check to see if we have any credentials saved
	 * 
	 * @return
	 */
	public boolean hasCredentials() {
		if ((getUsername() != null) && (getPassword() != null)) 
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
	 * Return the last viewed card
	 */
	public String getLastViewedCard() {
		return preferences.getString(KEY_LAST_VIEWED_CARD, null);
	}

	/**
	 * Set the last viewed card
	 */
	public void setLastViewedCard(String cardNumber) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(KEY_LAST_VIEWED_CARD, cardNumber);
		editor.commit();
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
		setSessionTimestamp();
	}

	/**
	 * Clear the Session ID
	 */
	public void clearSessionId() {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(KEY_SESSION_ID, null);
		editor.commit();
	}

	/**
	 * Has the Session ID
	 */
	public boolean hasSessionId() {
		if (preferences.getString(KEY_SESSION_ID, null) == null) 
			return false;
		return true;
	}

	/**
	 * Get a boolean for whether the session is current or not, and clear any old session
	 */
	public boolean isSessionCurrent() {
		long sessionTimestamp = preferences.getLong(KEY_SESSION_TIMESTAMP, 0);

		Date now = new Date();
		long diff = now.getTime() - sessionTimestamp;

		if (diff < (SESSION_LIFETIME_MINUTES * 60000)) {
			return true;
		}

		clearSessionId();
		return false;
	}

	/**
	 * Set a long representing the last stats send date
	 */
	public void setSessionTimestamp() {
		Date now = new Date();
		SharedPreferences.Editor editor = preferences.edit();
		editor.putLong(KEY_SESSION_TIMESTAMP, now.getTime());
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

package com.andybotting.oystermate.activity;

import com.andybotting.oystermate.R;
import com.andybotting.oystermate.provider.OysterProvider;
import com.andybotting.oystermate.provider.OysterProviderException;
import com.andybotting.oystermate.utils.PreferenceHelper;
import com.andybotting.oystermate.utils.UIUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends Activity {

	public ProgressDialog pd;
	private OysterProvider mProvider;

	private String mErrorMessage;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.login);
		mProvider = new OysterProvider();

		final Button signInButton = (Button) findViewById(R.id.button_sign_in);
		signInButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// Start with basic credentials check
				if (basicCredentialsCheck())
					// Perform a login and check the document returns something
					new GetDocument().execute();
			}
		});

		final Button signUpButton = (Button) findViewById(R.id.button_sign_up);
		signUpButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(OysterProvider.SIGN_UP_URL));
				startActivity(i);
			}
		});

		final Button forgotUsernameButton = (Button) findViewById(R.id.button_forgot_username);
		forgotUsernameButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(OysterProvider.FORGOT_USERNAME_URL));
				startActivity(i);
			}
		});

		final Button forgotPasswordButton = (Button) findViewById(R.id.button_forgot_password);
		forgotPasswordButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(OysterProvider.FORGOT_PASSWORD_URL));
				startActivity(i);
			}
		});

	}

	/**
	 * Simple check that username and password are filled out
	 */
	protected Boolean basicCredentialsCheck() {
		String username = getUsernameField();
		String password = getPasswordField();

		if ((username.length() > 2) && (password.length() > 2) && (username.contains("@"))) return true;

		UIUtils.showMessage(Login.this,	"Error", "Please ensure that your email address and password have been entered correctly.\n\nIf you're having trouble, please test your account details at https://account.tfl.gov.uk/Login first.");

		return false;
	}

	/**
	 * Get the username EditText field
	 */
	protected String getUsernameField() {
		EditText usernameField = (EditText) findViewById(R.id.username);
		return usernameField.getText().toString().trim();
	}

	/**
	 * Get the password EditText field
	 */
	protected String getPasswordField() {
		EditText passwordField = (EditText) findViewById(R.id.password);
		return passwordField.getText().toString().trim();
	}

	/**
	 * Save the username and password
	 */
	protected void saveCredentials() {
		PreferenceHelper preferenceHelper = new PreferenceHelper();
		preferenceHelper.setCredentials(getUsernameField(), getPasswordField());
	}

	/**
	 * Background task for fetching document
	 */
	private class GetDocument extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			mErrorMessage = null;
			pd = ProgressDialog.show(Login.this, "", getResources().getText(R.string.signing_in), true, false);
		}

		@Override
		protected String doInBackground(final Void... params) {
			String document = null;
			try {
				document = mProvider.performLogin(getUsernameField(), getPasswordField());
				if (document == null) mErrorMessage = "Error Signing in.";
			} catch (OysterProviderException e) {
				mErrorMessage = e.getMessage();
				e.printStackTrace();
			} catch (Exception e) {
				mErrorMessage = "Unable to connect to TfL. Please check your Internet connection.\n\n" + e.toString();
				e.printStackTrace();
			}
			return document;
		}

		@Override
		protected void onPostExecute(String document) {
			pd.dismiss();
			if (mErrorMessage != null) {
				UIUtils.showMessage(Login.this, "Login Error", mErrorMessage);
			} else {
				// Success
				saveCredentials();
				setResult(Activity.RESULT_OK, new Intent());
				finish();
			}
		}
	}

}

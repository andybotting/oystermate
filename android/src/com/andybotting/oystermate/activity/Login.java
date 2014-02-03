package com.andybotting.oystermate.activity;

import com.actionbarsherlock.app.ActionBar;
import com.andybotting.oystermate.R;
import com.andybotting.oystermate.provider.OysterProvider;
import com.andybotting.oystermate.provider.OysterProviderException;
import com.andybotting.oystermate.utils.PreferenceHelper;
import com.andybotting.oystermate.utils.UIUtils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;

public class Login extends SherlockActivity {

	public ProgressDialog pd;
	private OysterProvider mProvider;

	private String mErrorMessage;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.login);
		
		// Set up the Action Bar
		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(false);				
		
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
	 * Show about dialog window
	 */
	public void showAbout() {
		// Get the package name
		String heading = getResources().getText(R.string.app_name) + "\n";

		try {
			PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
			heading += "v" + pi.versionName + "\n\n";
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		// Build alert dialog
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(heading);
		View aboutView = getLayoutInflater().inflate(R.layout.dialog_about, null);
		dialogBuilder.setView(aboutView);
		dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});
		dialogBuilder.setCancelable(false);
		dialogBuilder.setIcon(R.drawable.ic_dialog_info);
		dialogBuilder.show();
	}
	
	/**
	 * Options menu
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.home, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Menu actions
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.menu_about:
			showAbout();
			return true;
		case android.R.id.home:
			finish();
			return true;
		}
		return false;
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
				setResult(SherlockActivity.RESULT_OK, new Intent());
				finish();
			}
		}
	}

}

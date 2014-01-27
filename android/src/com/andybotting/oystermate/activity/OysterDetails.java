package com.andybotting.oystermate.activity;

import com.andybotting.oystermate.objects.OysterCard;
import com.andybotting.oystermate.objects.AccountInfo;
import com.andybotting.oystermate.objects.TravelCard;
import com.andybotting.oystermate.provider.OysterProvider;
import com.andybotting.oystermate.utils.PreferenceHelper;
import com.andybotting.oystermate.utils.UIUtils;
import com.andybotting.oystermate.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class OysterDetails extends Activity {

	private static final String TAG = "OysterDetails";
	private static final boolean LOGV = Log.isLoggable(TAG, Log.INFO);

	public static final String TFL_URL = "https://oyster.tfl.gov.uk";

	// Menu items
	private static final int MENU_INFO = 0;
	private static final int MENU_REFRESH = 1;
	private static final int MENU_SIGN_OUT = 2;

	private PreferenceHelper mPreferenceHelper;
	private OysterProvider mProvider;

	private AccountInfo mAccountInfo;
	private OysterCard mOysterCard;
	private String mSelectedOysterCardNumber;

	private ViewGroup mOysterDetailsView;

	private Spinner mCardsSpinner;
	private ArrayAdapter<CharSequence> mAdapterForSpinner;

	private String mErrorMessage;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.oystercard_details);

		mPreferenceHelper = new PreferenceHelper();
		mProvider = new OysterProvider();

		refreshDetails();
	}

	/**
	 * Handle "refresh" title-bar action.
	 */
	public void onRefreshClick(View v) {
		refreshDetails();
	}

	/**
	 * Handle "map" title-bar action.
	 */
	public void onMapClick(View v) {
		// nothing yet
	}

	/**
	 * Handle Manage auto top-up click
	 */
	public void onManageAutoTopUpClick(View v) {
		UIUtils.launchWebView(this, TFL_URL + mOysterCard.getManageAutoTopUpURL());
	}

	/**
	 * Handle Add Top-up click
	 */
	public void onAddTopUpClick(View v) {
		UIUtils.launchWebView(this, TFL_URL + mOysterCard.getAddTopUpURL());
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
	 * Show a dialog message
	 */
	public void showErrorMessage(Context context, String title, String message, final boolean logOut) {
		final SpannableString s = new SpannableString(message);
		Linkify.addLinks(s, Linkify.ALL);

		final AlertDialog d = new AlertDialog.Builder(context).setTitle(title).setMessage(s).setIcon(R.drawable.ic_dialog_alert)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
						if (logOut) logout();
					}
				}).create();
		d.show();
		((TextView) d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
	}

	/**
	 * Results back from login activity
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (resultCode) {
		case Activity.RESULT_OK:
			new GetOysterAccountInfo().execute();
			break;
		case Activity.RESULT_CANCELED:
			finish();
			break;
		}

	}

	/**
	 * Create the options menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, MENU_INFO, 0, R.string.menu_info).setIcon(R.drawable.ic_menu_info_details);

		menu.add(0, MENU_REFRESH, 0, R.string.menu_refresh).setIcon(R.drawable.ic_menu_refresh);

		menu.add(0, MENU_SIGN_OUT, 0, R.string.menu_logout).setIcon(R.drawable.ic_menu_logout);

		return true;
	}

	/**
	 * Options item selected
	 */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {

		switch (item.getItemId()) {
		case MENU_INFO:
			showAbout();
			return true;
		case MENU_SIGN_OUT:
			logout();
			return true;
		case MENU_REFRESH:
			refreshDetails();
			return true;
		}
		return false;
	}

	/**
	 * Log out
	 */
	private void logout() {
		// Clear credentials, sign out from TfL and show login activity
		mPreferenceHelper.clearCredentials();
		new AccountLogout().execute();
		Intent intent = new Intent(this, Login.class);
		startActivityForResult(intent, 0);
	}

	/**
	 * Refresh details
	 */
	private void refreshDetails() {
		// If we haven't signed in yet
		if (!mPreferenceHelper.hasCredentials()) {
			Intent i = new Intent(this, Login.class);
			startActivityForResult(i, 0);
		} else {
			if (mSelectedOysterCardNumber != null) {
				new GetSelectedCardDetails().execute(mSelectedOysterCardNumber);
			} else {
				new GetOysterAccountInfo().execute();
			}
		}
	}

	/**
	 * Display a message in the middle of the screen, and hide the results
	 */
	private void displayMessage(String message) {
		((TextView) findViewById(R.id.message_view_text)).setText(message);
		findViewById(R.id.message_view).setVisibility(View.VISIBLE);
		findViewById(R.id.oyster_details_scroll_view).setVisibility(View.GONE);
		findViewById(R.id.loading_view).setVisibility(View.GONE);
	}

	/**
	 * Update refresh status icon/views
	 */
	private void updateRefreshStatus(boolean isRefreshing) {
		// Main window
		findViewById(R.id.oyster_details_scroll_view).setVisibility(isRefreshing ? View.GONE : View.VISIBLE);
		findViewById(R.id.message_view).setVisibility(isRefreshing ? View.GONE : View.VISIBLE);
		findViewById(R.id.loading_view).setVisibility(isRefreshing ? View.VISIBLE : View.GONE);

		// Refresh button
		findViewById(R.id.btn_title_refresh).setVisibility(isRefreshing ? View.GONE : View.VISIBLE);
		findViewById(R.id.title_refresh_progress).setVisibility(isRefreshing ? View.VISIBLE : View.GONE);
	}

	/**
	 * Update refresh status icon/views
	 */
	private void updateRefreshStatusCard(boolean isRefreshing) {
		// Main window
		findViewById(R.id.oyster_details_view).setVisibility(isRefreshing ? View.GONE : View.VISIBLE);
		findViewById(R.id.card_loading_view).setVisibility(isRefreshing ? View.VISIBLE : View.GONE);

		// Refresh button
		findViewById(R.id.btn_title_refresh).setVisibility(isRefreshing ? View.GONE : View.VISIBLE);
		findViewById(R.id.title_refresh_progress).setVisibility(isRefreshing ? View.VISIBLE : View.GONE);
	}

	/**
	 * Display the OysterCard Account
	 */
	private void displayOysterDetails(final AccountInfo accountInfo) {

		if (!accountInfo.hasOysterCards()) {
			displayMessage("No Oyster Cards could be found in your account.");
		} else {
			((TextView) findViewById(R.id.welcome)).setText(accountInfo.getWelcome());

			if (accountInfo.hasMultipleOysterCards()) {

				// If we have more than one card, show the spinner
				ViewGroup cardSelectView = (ViewGroup) findViewById(R.id.card_select_view);
				cardSelectView.setVisibility(View.VISIBLE);

				mAdapterForSpinner = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
				mAdapterForSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				mCardsSpinner = (Spinner) cardSelectView.findViewById(R.id.card_select_spinner);
				mCardsSpinner.setAdapter(mAdapterForSpinner);

				for (String oysterCardNumber : accountInfo.getOysterCardNumbers()) {
					mAdapterForSpinner.add(oysterCardNumber);
				}

				mCardsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
						mSelectedOysterCardNumber = mAccountInfo.getOysterCardNumber(position);
						if (LOGV) Log.i(TAG, "Setting last viewed: " + mSelectedOysterCardNumber);
						mPreferenceHelper.setLastViewedCard(mSelectedOysterCardNumber);
						if (mAccountInfo.hasOysterCard(mSelectedOysterCardNumber)) {
							mOysterCard = mAccountInfo.getOysterCard(position);
							displayOysterCardDetails(mOysterCard);
						} else {
							new GetSelectedCardDetails().execute(mSelectedOysterCardNumber);
						}

					}

					public void onNothingSelected(AdapterView<?> parent) {
						mSelectedOysterCardNumber = null;
					}

				});

				// Set our initial card to be the one we viewed last time
				if (mPreferenceHelper.getLastViewedCard() != null) {
					for (int i = 0; i < mAdapterForSpinner.getCount(); i++) {
						if (mPreferenceHelper.getLastViewedCard().contains(mAdapterForSpinner.getItem(i))) mCardsSpinner.setSelection(i);
					}
				}
			} else {
				ViewGroup cardSelectView = (ViewGroup) findViewById(R.id.card_select_view);
				cardSelectView.setVisibility(View.GONE);

				mSelectedOysterCardNumber = mAccountInfo.getOysterCardNumber(0);
				if (mAccountInfo.hasOysterCard(mSelectedOysterCardNumber)) {
					mOysterCard = mAccountInfo.getOysterCard(0);
					displayOysterCardDetails(mOysterCard);
				} else {
					new GetSelectedCardDetails().execute(mSelectedOysterCardNumber);
				}
			}
		}
	}

	/**
	 * Display oyster card details
	 */
	private void displayOysterCardDetails(OysterCard oysterCard) {

		try {

			// Clean out any old views to start fresh
			mOysterDetailsView = (ViewGroup) findViewById(R.id.oyster_details_view);
			mOysterDetailsView.removeAllViews();

			// Add card info and balance
			View cardInfoView = buildCardInfoView(oysterCard);

			// If we have multiple cards, don't show the card header - we'll already know
			// what the card number is
			if (mAccountInfo.hasMultipleOysterCards()) cardInfoView.findViewById(R.id.card_overview_heading).setVisibility(View.GONE);

			mOysterDetailsView.addView(cardInfoView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			// Auto Top-Up
			if (oysterCard.hasAutoTopUp()) {
				View autoTopUpView = buildAutoTopUpView(oysterCard);
				mOysterDetailsView.addView(autoTopUpView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			}

			// Add a Travel Card by inflating a new view
			for (TravelCard travelCard : oysterCard.getTravelCards()) {
				View travelCardView = buildTravelCardView(travelCard);
				mOysterDetailsView.addView(travelCardView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			}

		} catch (Exception e) {
			e.printStackTrace();
			UIUtils.showMessage(OysterDetails.this, "OysterMate Error", "There was an error showing the results:\n" + e.toString());
		}
	}

	/**
	 * Build a view for each travelcard
	 * 
	 * @param travelCard
	 * @return
	 */
	private View buildCardInfoView(OysterCard oysterCard) {
		View cardInfoView = getLayoutInflater().inflate(R.layout.detail_cardinfo, null);

		// Set the labels
		((TextView) cardInfoView.findViewById(R.id.card_overview_heading)).setText("Card " + oysterCard.getCardNumber());

		// Pay as you go
		((TextView) cardInfoView.findViewById(R.id.payg_balance)).setText(oysterCard.getPayAsYouGoBalance());

		return cardInfoView;
	}

	/**
	 * Build a view for each travelcard
	 * 
	 * @param travelCard
	 * @return
	 */
	private View buildAutoTopUpView(OysterCard oysterCard) {
		View autoTopUpView = getLayoutInflater().inflate(R.layout.detail_autotopup, null);
		((TextView) autoTopUpView.findViewById(R.id.auto_topup)).setText(oysterCard.getAutoTopUp());
		return autoTopUpView;
	}

	/**
	 * Build a view for each travelcard
	 * 
	 * @param travelCard
	 * @return
	 */
	private View buildTravelCardView(TravelCard travelCard) {
		View travelCardView = getLayoutInflater().inflate(R.layout.detail_travelcard, null);
		((TextView) travelCardView.findViewById(R.id.travelcard_heading)).setText(travelCard.getName());
		((TextView) travelCardView.findViewById(R.id.travelcard_date)).setText(travelCard.getDateString());

		final ProgressBar progressBar = (ProgressBar) travelCardView.findViewById(R.id.travelcard_progress);
		progressBar.setProgress(travelCard.getDateProgress());

		((TextView) travelCardView.findViewById(R.id.travelcard_status)).setText(travelCard.getDateProgress() + "% complete");

		return travelCardView;
	}

	/**
	 * Background task for fetching card details
	 */
	private class GetSelectedCardDetails extends AsyncTask<String, Void, OysterCard> {

		@Override
		protected void onPreExecute() {
			mErrorMessage = null;
			updateRefreshStatusCard(true);
		}

		protected OysterCard doInBackground(String... oysterCardNumber) {
			// Assume only one card number given in params
			OysterCard oysterCard = null;

			try {
				oysterCard = mProvider.getOysterCard(oysterCardNumber[0]);
			} catch (Exception e) {
				e.printStackTrace();
				mErrorMessage = e.toString();
			}
			return oysterCard;
		}

		@Override
		protected void onPostExecute(OysterCard oysterCard) {

			if (mErrorMessage != null) {
				UIUtils.showMessage(OysterDetails.this, "Oyster Mate Error", "There was an error fetching your Oyster card details: \n" + mErrorMessage);
				displayMessage("No Results.");
				updateRefreshStatusCard(false);
			} else {
				mOysterCard = oysterCard;
				displayOysterCardDetails(mOysterCard);
				updateRefreshStatusCard(false);
			}

		}
	}

	/**
	 * Background task for fetching Account details
	 */
	private class GetOysterAccountInfo extends AsyncTask<Void, Void, AccountInfo> {

		@Override
		protected void onPreExecute() {
			mErrorMessage = null;
			updateRefreshStatus(true);
		}

		@Override
		protected AccountInfo doInBackground(final Void... params) {
			mAccountInfo = null;

			try {
				mAccountInfo = mProvider.getOysterCardAccountInfo();
			} catch (Exception e) {
				e.printStackTrace();
				mErrorMessage = e.toString();
			}

			return mAccountInfo;
		}

		@Override
		protected void onPostExecute(AccountInfo mAccountInfo) {
			updateRefreshStatus(false);

			if (mErrorMessage != null) {

				boolean doLogout = false;
				if (mErrorMessage.contains("transitioned") || mErrorMessage.contains("attempts")) doLogout = true;

				showErrorMessage(OysterDetails.this, "Oyster Mate Error", "There was an error fetching your Oyster account details: \n" + mErrorMessage,
						doLogout);
				displayMessage("No Results.");
			} else {
				displayOysterDetails(mAccountInfo);
			}
		}
	}

	/**
	 * Background task for fetching OysterCard details
	 */
	private class AccountLogout extends AsyncTask<Void, Void, Boolean> {
		Boolean isLoggedOut = false;

		@Override
		protected void onPreExecute() {
			mErrorMessage = null;
			displayMessage("Logging out...");
		}

		@Override
		protected Boolean doInBackground(final Void... params) {

			try {
				isLoggedOut = mProvider.performLogout();
			} catch (Exception e) {
				e.printStackTrace();
				mErrorMessage = e.toString();
			}

			return isLoggedOut;
		}

		protected void onPostExecute(Boolean isLoggedOut) {
			updateRefreshStatus(false);

			if (mErrorMessage != null) {
				UIUtils.showMessage(OysterDetails.this, "Oyster Mate Error", "There was an error signing out of your Oyster Account: \n" + mErrorMessage);
				displayMessage("Error.");
			} else {
				displayOysterDetails(mAccountInfo);
			}
		}
	}

}
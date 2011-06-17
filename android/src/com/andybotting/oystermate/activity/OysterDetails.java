package com.andybotting.oystermate.activity;

import com.andybotting.oystermate.objects.OysterCard;
import com.andybotting.oystermate.objects.TravelCard;
import com.andybotting.oystermate.provider.OysterProvider;
import com.andybotting.oystermate.utils.PreferenceHelper;
import com.andybotting.oystermate.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

public class OysterDetails extends Activity {
	
	public static final String TFL_URL = "https://oyster.tfl.gov.uk";
	
	// Menu items
	private static final int MENU_SIGN_OUT = 0;
	private static final int MENU_REFRESH = 1;
	
	private PreferenceHelper mPreferenceHelper;
	private OysterProvider mProvider;
	private OysterCard mOysterCard;

	private String mErrorMessage;
	
	
    @Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);	  
        setContentView(R.layout.details);
                
        mPreferenceHelper = new PreferenceHelper();
        mProvider = new OysterProvider();

        kickOff();
    }
    
    
    /** 
     * Handle "refresh" title-bar action. 
     */
    public void onRefreshClick(View v) {
    	kickOff();
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
    	lauchWebView(TFL_URL + mOysterCard.getManageAutoTopUpURL());
    }
    
    /** 
     * Handle Add Top-up click 
     */
    public void onAddTopUpClick(View v) {
		lauchWebView(TFL_URL + mOysterCard.getAddTopUpURL());

    }    
    
    
    /**
     * Launch the web view with the given URL
     * @param url
     */
    public void lauchWebView(String url) {
		Bundle bundle = new Bundle();
		bundle.putString(WebLaunch.INTENT_URL, url);
		Intent intent = new Intent(OysterDetails.this, WebLaunch.class);
		intent.putExtras(bundle);
		startActivity(intent);   	
    }
        
    
    /**
     * Start the activity
     */
    protected void kickOff() {
    	if (!mPreferenceHelper.hasCredentials()) {
        	Intent i = new Intent(this, Login.class);
        	startActivityForResult(i, 0);
        }
        else {
        	new GetOysterDetails().execute();
        }
    }

	
	/**
	 * Results back from login activity
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (resultCode) {
			case Activity.RESULT_OK:
				new GetOysterDetails().execute();
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
		
		menu.add(0, MENU_SIGN_OUT, 0, R.string.logout)
			.setIcon(R.drawable.ic_menu_logout);
		
		menu.add(0, MENU_REFRESH, 0, R.string.refresh)
			.setIcon(R.drawable.ic_menu_refresh);
	
		return true;
	}
	
	
	/**
	 * Options item selected
	 */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		
		switch (item.getItemId()) {
			case MENU_SIGN_OUT:
				mPreferenceHelper.clearCredentials();
				kickOff();
				return true;
			case MENU_REFRESH:
				new GetOysterDetails().execute();
				return true;
		}	
		return false;
	}
	
	
	/**
	 * Display a message in the middle of the screen, and hide the results
	 */
	private void displayNoResults(String message) {
		((TextView)findViewById(R.id.message_view_text)).setText(message);
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
	 * Show a dialog message
	 */
	private void showMessage(String title, String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        if (title != null)
        	dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.setIcon(R.drawable.icon);
        dialogBuilder.show();
	}
	
	
	/**
	 * Display oyster card details
	 */
	private void displayDetails(final OysterCard oysterCard) {

		// Clean up season tickets
		ViewGroup travelCardsLayout = (ViewGroup) findViewById(R.id.travel_cards_layout);
		travelCardsLayout.removeAllViews();

		try {
			// Set the labels
			((TextView)findViewById(R.id.welcome)).setText(oysterCard.getWelcome());
			((TextView)findViewById(R.id.card_number)).setText("Card Number: " + oysterCard.getCardNumber());
		
			// Pay as you go
			if (oysterCard.hasPayAsYouGoBalance()) {
				((TextView)findViewById(R.id.payg_balance)).setText(oysterCard.getPayAsYouGoBalance());
			}

			// Auto Top-Up
			if (oysterCard.hasAutoTopUp()) {
				((TextView)findViewById(R.id.auto_topup)).setText(oysterCard.getAutoTopUp());
			}

			// Season ticket
			if (oysterCard.hasSeasonTicketMessage()) {
				TextView seasonTicketMessage = ((TextView)findViewById(R.id.season_ticket_message));
				seasonTicketMessage.setText(oysterCard.getSeasonTicketMessage());
				seasonTicketMessage.setVisibility(View.VISIBLE);
			}
			
			// Add a Travel Card by inflating a new view
			for (TravelCard travelCard : oysterCard.getTravelCards()) {
				View travelCardView = buildTravelCardView(travelCard);
				travelCardsLayout.addView(travelCardView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				
		        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.travelcard_progress);
		        progressBar.setProgress(travelCard.getDateProgress());
			}
				
		}
		catch (Exception e) {
			e.printStackTrace();
			showMessage("OysterMate Error", "There was an error showing the results:\n" + e.toString());
		}
	}
	
	
	/**
	 * Build a view for each travelcard
	 * @param travelCard
	 * @return
	 */
	private View buildTravelCardView(TravelCard travelCard) {
		View detailView = getLayoutInflater().inflate(R.layout.travelcard, null);
		((TextView) detailView.findViewById(R.id.travelcard_heading)).setText(travelCard.getName());
		((TextView) detailView.findViewById(R.id.travelcard_date)).setText(travelCard.getDateString());
		return detailView;
	}
	
    /**
     * Background task for fetching tram times
     */
	private class GetOysterDetails extends AsyncTask<Void, Void, OysterCard> {

		@Override
		protected void onPreExecute() {
			mErrorMessage = null;
			//pd = ProgressDialog.show(OysterDetails.this, "", getResources().getText(R.string.fetching_data), true, false);
			updateRefreshStatus(true);
		}

		@Override
		protected OysterCard doInBackground(final Void... params) {
			OysterCard oysterCard = null;
			
	    	try {
	    		oysterCard = mProvider.getOysterCard();
			} 
	    	catch (Exception e) {
	    		e.printStackTrace();
	    		mErrorMessage = e.toString();
			}
	    	mOysterCard = oysterCard;
	    	return oysterCard;
		}

		@Override
		protected void onPostExecute(OysterCard oysterCard) {
			//pd.dismiss();
			updateRefreshStatus(false);

			if (mErrorMessage != null) {
				showMessage("OysterMate Error", "There was an error fetching your Oyster card details: \n" + mErrorMessage);
				displayNoResults("No Results.");
			}
			else {
				displayDetails(oysterCard);
			}
			
		}
	}
	
	
    
    
}
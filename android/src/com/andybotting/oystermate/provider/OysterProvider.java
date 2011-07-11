package com.andybotting.oystermate.provider;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.andybotting.oystermate.objects.OysterCard;
import com.andybotting.oystermate.objects.AccountInfo;
import com.andybotting.oystermate.objects.TravelCard;
import com.andybotting.oystermate.provider.HttpConnection;

public class OysterProvider {

	private static final String TAG = "OysterMate";
	private static final boolean LOGV = Log.isLoggable(TAG, Log.INFO);

	public static final String LOGIN_POST_URL = "https://oyster.tfl.gov.uk/oyster/security_check";
	public static final String LOGGED_IN_URL = "https://oyster.tfl.gov.uk/oyster/entry.do";
	public static final String DETAILS_URL = "https://oyster.tfl.gov.uk/oyster/loggedin.do";
	public static final String JOURNEY_HISTORY_URL = "https://oyster.tfl.gov.uk/oyster/ppvStatementPrint.do";
	public static final String SELECT_CARD_URL = "https://oyster.tfl.gov.uk/oyster/selectCard.do";
	
	private Pattern pattern;
	private Matcher matcher;
	
	
	/**
	 * Search for one instance of a string
	 */
	public String searchItem(String searchPattern, String document) {

		String match = null;
		pattern = Pattern.compile(searchPattern, Pattern.MULTILINE|Pattern.DOTALL);
		matcher = pattern.matcher(document);
		
		if (matcher.find())
			match = matcher.group(1);

		if (LOGV) Log.i(TAG, "Searching for: " + searchPattern + " returned: " + match);
		return match;
	}
	
	
	/**
	 * Get the card balance
	 */
	public String getPAYGBalance(String document) {
		return searchItem(">Balance:.*?(-?\\d+\\.\\d+)</span>", document);
	}
	
	
	/**
	 * Get the card number
	 */
	public String getCardNo(String document) {
		return searchItem("<h2>Card No: (\\d+)</h2>", document);
	}
	
	
	/**
	 * Get the Auto Top-Up value
	 */
	public String getAutoTopUp(String document) {
		return searchItem(">Auto top up:.*?&#163;(\\d+\\.\\d+)", document);
	}
	
	
	/**
	 * Get the welcome message
	 */
	public String getWelcomeMessage(String document) {
		return searchItem("<p id=\"welcome\">(.*?)</p>", document);
	}
	
	
	/**
	 * Get the season ticket message 
	 */
	public String getSeasonTicketMessage(String document) {
		return searchItem("<h3>Season tickets</h3>.*?<span class=\"content\">(.*?)</span>", document);
	}
	
	
	/**
	 * Check to see if there are multiple cards on this account
	 */
	public boolean hasMultipleCards(String document) {
		String search = searchItem("(select name=\"cardId\" id=\"select_card_no\")", document);
		if (search == null)
			return false;
		return true;
	}
	
	
	
	/**
	 * Return the Oyster Card numbers found for the account 
	 */
	public List<String> getOysterCardNumbers(String document) {
		
		List<String> oysterCardNumbers = new ArrayList<String>();
		
		String searchPattern = ">(\\d{12})</option>";
		
		pattern = Pattern.compile(searchPattern);
		matcher = pattern.matcher(document);
		
        while (matcher.find()) {
        	oysterCardNumbers.add(matcher.group(1));
        }
        
        return oysterCardNumbers;
	}
	
	
	
	/**
	 * Get the season ticket message 
	 */
	public String getErrorMessage(String document) {
		return searchItem("<div id=\"errormessage\"><ul><li>(.*?)</li></ul></div>", document);
	}	
	
	
	/**
	 * Get the travel cards
	 */
	public List<TravelCard> getTravelCards(String document) {
		
		List<TravelCard> travelCards = new ArrayList<TravelCard>();
		
		String searchPattern = "<div class=\"row\">.*?<h3>(.*?)</h3>.*?</div>.*?<span class=\"label\">Start date:</span>.*?<span class=\"content\">(.*?)</span>.*?<span class=\"label\">End date:</span>.*?<span class=\"content\">(.*?)</span>";

		pattern = Pattern.compile(searchPattern, Pattern.MULTILINE|Pattern.DOTALL);
		matcher = pattern.matcher(document);
		
        while (matcher.find()) {
        	try {
        		String name = matcher.group(1);
        		Date startDate = parseDateString(matcher.group(2) + " 00:00:00");
        		Date endDate = parseDateString(matcher.group(3) + " 23:59:59");
        		TravelCard travelCard = new TravelCard(name, startDate, endDate);
        		travelCards.add(travelCard);
        	} catch (ParseException e) {
        		e.printStackTrace();
        		// Ignore this error
        	}
        }
		
        return travelCards;
	}	

	
	/**
	 * Get the Add Top-Up URL 
	 */
	public String getAddTopUpURL(String document) {
		return searchItem("<a href=\"([^>]*)\">Add/renew/top-up ticket</a>", document);
	}	
	

	/**
	 * Get the Manage Auto Top-Up URL
	 */
	public String getManageAutoTopUpURL(String document) {
		return searchItem("<a href=\"([^>]*)\">Manage Auto top-up</a>", document);
	}	

	
//	/**
//	 * Get the journey history
//	 * @param document
//	 * @return List<TravelCard>
//	 */
//	public List<TravelCard> getJourneyHistory(String document) {
//		
//		List<TravelCard> travelCards = new ArrayList<TravelCard>();
//		
//		String searchPattern = "<tr><td>(.*?)</td><td>(\\d+:\\d+)</td><td>(.*?)</td><td>\\s?(.*?)</td><td align=\"right\">\\s?(-?\\s?\\d+\\.\\d+)</td><td align=\"center\">(.*?)</td><td align=\"right\">\\s?(-?\\s?\\d+\\.\\d+)</td></tr>";
//		pattern = Pattern.compile(searchPattern);
//		
//		// Clean up our document a little
//		document = document.replace("\t", "");
//		document = document.replace("\n", "");
//		document = document.replace("&pound;", "");
//		
//		matcher = pattern.matcher(document);
//		
//		String oldDate = null;
//        while (matcher.find()) {
//        	Journey journey = new Journey();
//        	
//        	String dateString = null;
//        	if (matcher.group(1).contains(""))
//        		dateString = oldDate;
//        	else
//        		dateString = matcher.group(1);
//        	
//        	String timeString = matcher.group(1);
//        	
//        }
//		
//        return travelCards;
//	}		
	
	
//	<tr>
//	
//	<td>10/06/11</td>
//	
//	<td>08:43</td>
//	<td>
//	
//	Brentford [National Rail]
//	
//	</td>
//	<td> Exit</td>
//
//	<td align="right">
//		&pound;4.60</td>
//	<td align="center"></td>
//	<td align="right">&pound;15.80</td>
//	
//</tr>
		
	/**
	 * Parse the date string
	 */
	private Date parseDateString(String dateString) throws ParseException  {
		// 09/06/2011
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		date = df.parse(dateString);
		return date;
	}
	
	
	/**
	 * Get the oystercard for a given card number
	 */
	public OysterCard getOysterCard(String oysterCardNumber) throws IOException, OysterProviderException {
		String oysterCardDetailsPage = getSelectedCardDetails(oysterCardNumber);
		return parseOysterCard(oysterCardDetailsPage);
	}
	
	
	/**
	 * Get the Oyster Card account info
	 */
	public AccountInfo getOysterCardAccountInfo() throws IOException, OysterProviderException {
		AccountInfo accountInfo = new AccountInfo();
		
		String oysterCardDetailsPage = getOysterCardDetailsPage();
		
		String welcome = getWelcomeMessage(oysterCardDetailsPage);

		
		accountInfo.setWelcome(welcome);		
	
		
		if (hasMultipleCards(oysterCardDetailsPage)) {
			List<String> oysterCardNumbers = getOysterCardNumbers(oysterCardDetailsPage);
			accountInfo.setOysterCardNumbers(oysterCardNumbers);
		}
		else {
			// Add the card to the account info
			OysterCard oysterCard = parseOysterCard(oysterCardDetailsPage);
			accountInfo.addOysterCardNumber(oysterCard.getCardNumber());
			accountInfo.addOysterCard(oysterCard);	
		}
		
		return accountInfo;
	}


	/**
	 * Parse the OysterCard html and return an object
	 */
	public OysterCard parseOysterCard(String oysterCardDetailsPage) throws IOException, OysterProviderException {
		
		OysterCard oysterCard = new OysterCard();
		

		String cardNumber = getCardNo(oysterCardDetailsPage);
		String addTopUpURL = getAddTopUpURL(oysterCardDetailsPage);
		String manageAutoTopUpURL = getManageAutoTopUpURL(oysterCardDetailsPage);
		String seasonTicketMessage = getSeasonTicketMessage(oysterCardDetailsPage);
		List<TravelCard> travelCards = getTravelCards(oysterCardDetailsPage);
		
		oysterCard.setCardNumber(cardNumber);
		oysterCard.setAddTopUpURL(addTopUpURL);
		oysterCard.setManageAutoTopUpURL(manageAutoTopUpURL);	
		oysterCard.setSeasonTicketMessage(seasonTicketMessage);
		oysterCard.setTravelCards(travelCards);
		
		String autoTopUpS = getAutoTopUp(oysterCardDetailsPage);
		String paygBalanceS = getPAYGBalance(oysterCardDetailsPage);
				
		if (paygBalanceS != null) {
			try {
				double paygBalance = Double.parseDouble(paygBalanceS);
				oysterCard.setPayAsYouGoBalance(paygBalance);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		
		if (autoTopUpS != null) {
			try { 
				double autoTopUp = Double.parseDouble(autoTopUpS);
				oysterCard.setAutoTopUp(autoTopUp);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				// Nothing
			}
		}
		
		return oysterCard;
	}
	
	
	
	/**
	 * Get the Oyster card details web page
	 */
	public String getOysterCardDetailsPage() throws IOException, OysterProviderException {
		return getDocumentContent(DETAILS_URL);
	}
	
	
	/**
	 * Get the journey history web page
	 */
	public String getJourneyHistoryPage() throws IOException, OysterProviderException {
		return getDocumentContent(DETAILS_URL);
	}	
	
	
	/**
	 * Get the Oyster Card details for a given card number, if multiple cards exist 
	 */
	public String getSelectedCardDetails(String oysterCardNumber) throws IOException, OysterProviderException {
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	    nameValuePairs.add(new BasicNameValuePair("method", "input"));
	    nameValuePairs.add(new BasicNameValuePair("cardId", oysterCardNumber));
	    
	    HttpConnection httpConn = new HttpConnection();
	    String document = httpConn.postURL(SELECT_CARD_URL, nameValuePairs);
	    
		String errorMessage = getErrorMessage(document);
		if (errorMessage != null)
			throw new OysterProviderException(errorMessage);

		return document;
	}


	/**
	 * Perform login specifiying username and password
	 */
	public String performLogin(String username, String password) throws IOException, OysterProviderException {
		if (LOGV) Log.i(TAG, "Logging in...");
		
		HttpConnection httpConn = new HttpConnection();
		String document = httpConn.performLogin(username, password);

		String errorMessage = getErrorMessage(document);
		if (errorMessage != null)
			throw new OysterProviderException(errorMessage);

		return document;
	}
	
	
	/**
	 * Get the document content from a given url
	 */
	public String getDocumentContent(String url) throws IOException, OysterProviderException {
		if (LOGV) Log.i(TAG, "Getting web page content...");
		
		HttpConnection httpConn = new HttpConnection();
		String document = httpConn.getURL(url);
		
		String errorMessage = getErrorMessage(document);
		if (errorMessage != null)
			throw new OysterProviderException(errorMessage);

		return document;
	}
	
}

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

import android.util.Log;

import com.andybotting.oystermate.objects.OysterCard;
import com.andybotting.oystermate.objects.TravelCard;
import com.andybotting.oystermate.provider.HttpConnection;
import com.andybotting.oystermate.utils.PreferenceHelper;

public class OysterProvider {

	private static final String TAG = "OysterMate";
	private static final boolean LOGV = Log.isLoggable(TAG, Log.INFO);

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
	 * @param document
	 * @return String
	 */
	public String getPAYGBalance(String document) {
		return searchItem(">Balance:.*?&pound;(\\d+\\.\\d+)</span>", document);
	}
	
	
	/**
	 * Get the card number
	 * @param document
	 * @return String 
	 */
	public String getCardNo(String document) {
		return searchItem("<h2>Card No: (\\d+)</h2>", document);
	}
	
	
	/**
	 * Get the Auto Top-Up value
	 * @param document
	 * @return String
	 */
	public String getAutoTopUp(String document) {
		return searchItem(">Auto top up:.*?&#163;(\\d+\\.\\d+)", document);
		
	}
	
	
	/**
	 * Get the welcome message
	 * @param document
	 * @return String
	 */
	public String getWelcomeMessage(String document) {
		return searchItem("<p id=\"welcome\">(.*?)</p>", document);
	}
	
	
	/**
	 * Get the season ticket message 
	 * @param document
	 * @return String
	 * @throws OysterProviderException
	 */
	public String getSeasonTicketMessage(String document) {
		return searchItem("<h3>Season tickets</h3>.*?<span class=\"content\">(.*?)</span>", document);
	}	
	
	
	/**
	 * Get the travel cards
	 * @param document
	 * @return List<TravelCard>
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
	 * @param document
	 * @return String
	 */
	public String getAddTopUpURL(String document) {
		return searchItem("<a href=\"([^>]*)\">Add/renew/top-up ticket</a>", document);
	}	
	

	/**
	 * Get the Manage Auto Top-Up URL
	 * @param document
	 * @return String
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
	 * @param dateString
	 * @return Date
	 */
	private Date parseDateString(String dateString) throws ParseException  {
		// 09/06/2011
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		date = df.parse(dateString);
		return date;
	}
	
	
	/**
	 * Get the Oyster Card from the document
	 */
	public OysterCard getOysterCard() throws IOException, OysterProviderException {
		
		OysterCard oysterCard = new OysterCard();
		String document = getDocumentContent();
		
		String welcome = getWelcomeMessage(document);
		String cardNumber = getCardNo(document);
		String autoTopUpS = getAutoTopUp(document);
		String paygBalanceS = getPAYGBalance(document);
		String addTopUpURL = getAddTopUpURL(document);
		String manageAutoTopUpURL = getManageAutoTopUpURL(document);
		String seasonTicketMessage = getSeasonTicketMessage(document);
		List<TravelCard> travelCards = getTravelCards(document);
		
		try {
			double paygBalance = Double.parseDouble(paygBalanceS);
			oysterCard.setPayAsYouGoBalance(paygBalance);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			// Nothing
		}
		
		try { 
			double autoTopUp = Double.parseDouble(autoTopUpS);
			oysterCard.setAutoTopUp(autoTopUp);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			// Nothing
		}
		
		oysterCard.setWelcome(welcome);
		oysterCard.setCardNumber(cardNumber);
		oysterCard.setAddTopUpURL(addTopUpURL);
		oysterCard.setManageAutoTopUpURL(manageAutoTopUpURL);
		oysterCard.setSeasonTicketMessage(seasonTicketMessage);
		oysterCard.setTravelCards(travelCards);
		
		return oysterCard;
		
	}
	
	
	/**
	 * Get the document content using saved credentials
	 * @throws IOException
	 * @throws OysterProviderException 
	 */
	public String getDocumentContent() throws IOException, OysterProviderException {
		PreferenceHelper preferenceHelper = new PreferenceHelper();
		String username = preferenceHelper.getUsername();
		String password = preferenceHelper.getPassword();
		return getDocumentContent(username, password);
	}
	
	
	/**
	 * Get the document content specifiying username and password
	 * @throws IOException 
	 * @throws OysterProviderException 
	 */
	public String getDocumentContent(String username, String password) throws IOException, OysterProviderException {
		if (LOGV) Log.i(TAG, "Getting web page content...");
		
		HttpConnection httpConn = new HttpConnection();
		String document = httpConn.fetchDocument(username, password);

		matcher = Pattern.compile("<div id=\"errormessage\"><ul><li>(.*?)</li></ul></div>").matcher(document);
		if (matcher.find())
			throw new OysterProviderException(matcher.group(1).toString());

		return document;

	}
	
}

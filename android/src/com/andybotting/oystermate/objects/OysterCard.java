package com.andybotting.oystermate.objects;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OysterCard {

    private String cardNumber;
	private double payAsYouGoBalance = 0.0;
	private double autoTopUpValue = 0.0;
	private String manageAutoTopUpURL;
	private String addTopUpURL;
	private String seasonTicketMessage;
	private List<TravelCard> travelCards;
	private List<Journey> journeys;

	
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	
	public String getCardNumber() {
		return cardNumber;
	}
	
	public boolean hasPayAsYouGoBalance() {
		if (payAsYouGoBalance == 0.0)
			return false;
		return true;
	}
	
	public void setPayAsYouGoBalance(double payAsYouGoBalance) {
		this.payAsYouGoBalance = payAsYouGoBalance;
	}
	
	public String getPayAsYouGoBalance() {
		NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.UK);
		return currencyFormatter.format(payAsYouGoBalance);
	}
	
	public boolean hasAutoTopUp() {
		if (autoTopUpValue == 0.0)
			return false;
		return true;
	}
	
	public void setAutoTopUp(double autoTopUp) {
		this.autoTopUpValue = autoTopUp;
	}
	
	public String getAutoTopUp() {
		NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.UK);
		return currencyFormatter.format(autoTopUpValue);
	}
	
	public void setManageAutoTopUpURL(String manageAutoTopUpURL) {
		this.manageAutoTopUpURL = manageAutoTopUpURL;
	}

	public String getManageAutoTopUpURL() {
		return manageAutoTopUpURL;
	}

	public boolean hasAddTopUpURL() {
		if (addTopUpURL == null)
			return false;
		return true;
	}
	
	public void setAddTopUpURL(String addTopUpURL) {
		this.addTopUpURL = addTopUpURL;
	}

	public String getAddTopUpURL() {
		return addTopUpURL;
	}
	
	public boolean hasSeasonTicketMessage() {
		if (seasonTicketMessage == null)
			return false;
		return true;
	}
	
	public void setSeasonTicketMessage(String seasonTicketMessage) {
		this.seasonTicketMessage = seasonTicketMessage;
	}

	public String getSeasonTicketMessage() {
		return seasonTicketMessage;
	}

	
	public boolean hasTravelCards() {
		return travelCards.isEmpty();
	}
	
	public void setTravelCards(List<TravelCard> travelCards) {
		this.travelCards = travelCards;
	}
	
	public List<TravelCard> getTravelCards() {
		return travelCards;
	}
	
	
	public void hasJourneys() {
		journeys.isEmpty();
	}
	
	public void setJourneys(List<Journey> journeys) {
		this.journeys = journeys;
	}
	
	public List<Journey> getJourneys() {
		return journeys;
	}
	
}

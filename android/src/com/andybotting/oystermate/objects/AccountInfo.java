package com.andybotting.oystermate.objects;

import java.util.ArrayList;
import java.util.List;

public class AccountInfo {
	
	private String welcome;
	private List<String> oysterCardNumbers = new ArrayList<String>();
	private List<OysterCard> oysterCards = new ArrayList<OysterCard>();
	private String manageAutoTopUpURL;
	private String addTopUpURL;


	public void setWelcome(String welcome) {
		this.welcome = welcome;
	}
	
	public String getWelcome() {
		return welcome;
	}
	
	public void setOysterCardNumbers(List<String> oysterCardNumbers) {
		this.oysterCardNumbers = oysterCardNumbers;
	}	
	
	public List<String> getOysterCardNumbers() {
		return oysterCardNumbers;
	}	

	public void addOysterCardNumber(String oysterCardNumber) {
		this.oysterCardNumbers.add(oysterCardNumber);
	}
	
	public int getOysterCardNumbersCount() {
		return oysterCardNumbers.size();
	}
	
	public boolean hasOysterCards() {
		if (oysterCardNumbers.size() > 0)
			return true;
		return false;
	}
	
	public boolean hasMultipleOysterCards() {
		if (oysterCardNumbers.size() > 1)
			return true;
		return false;
	}
	
	public String getOysterCardNumber(int index) {
		return oysterCardNumbers.get(index);
	}
	
	public boolean hasOysterCard(String oysterCardNumber) {
		for (OysterCard oysterCard : this.oysterCards) {
			if (oysterCard.getCardNumber().contentEquals(oysterCardNumber))
				return true;
		}
		return false;
	}

	public void addOysterCard(OysterCard oysterCard) {
		this.oysterCards.add(oysterCard);
	}

	public List<OysterCard> getOysterCards() {
		return oysterCards;
	}

	public OysterCard getOysterCard(int index) {
		return oysterCards.get(index);
	}
	
	public int getOysterCardCount() {
		return oysterCards.size();
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




	
}

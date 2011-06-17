package com.andybotting.oystermate.objects;

import java.util.Date;

public class Journey {

	private Date timestamp;
	private String location = null;
	private String action = null;
	private int fare = 0;
	private int priceCap = 0;
	private int balance = 0;
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public String getAction() {
		return action;
	}
	
	public void setFare(int fare) {
		this.fare = fare;
	}
	
	public int getFare() {
		return fare;
	}
	
	public void setPriceCap(int priceCap) {
		this.priceCap = priceCap;
	}
	
	public int getPrice_cap() {
		return priceCap;
	}
	
	public void setBalance(int balance) {
		this.balance = balance;
	}
	
	public int getBalance() {
		return balance;
	}
	
}

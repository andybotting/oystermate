package com.andybotting.oystermate.objects;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TravelCard {

    private String name;
	private Date startDate;
	private Date endDate;
	
	public TravelCard(String name, Date startDate, Date endDate) {
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getEndDate() {
		return endDate;
	}
	
	
	/**
	 * Return a specially formatted date string
	 * @return
	 */
	public String getDateString() {
		DateFormat df = new SimpleDateFormat("EEE, d MMM yyy");
		String startDate = df.format(getStartDate());
		String endDate = df.format(getEndDate());
		return String.format("%s - %s", startDate, endDate);
	}
	
	/**
	 * Return the percentage of the period between two dates
	 * @return
	 */
	public int getDateProgress() {
		float start = startDate.getTime();
		float end = endDate.getTime();
		float now = new Date().getTime();
		int percentage = (int)((now - start)/(end - start) * 100);
		return percentage;
	}
	
	
	public String toString() {
		return name;
	}
	
}

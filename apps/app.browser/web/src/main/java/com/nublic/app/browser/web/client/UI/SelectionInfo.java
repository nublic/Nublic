package com.nublic.app.browser.web.client.UI;


public class SelectionInfo {
	public String title;
	public String firstLine;
	public String secondLine;
	public String date;
	public String imageURL;
	public String onClickURL;
	
	public SelectionInfo(String title, String firstLine, String secondLine, String date, String imageURL, String onClickURL) {
		this.title = title;
		this.firstLine = firstLine;
		this.secondLine = secondLine;
		this.date = date;
		this.imageURL = imageURL;
		this.onClickURL = onClickURL;
	}
	
	public SelectionInfo(String title, String firstLine, String secondLine, String date, String imageURL) {
		this(title, firstLine, secondLine, date, imageURL, null);
	}

}

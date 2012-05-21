package com.nublic.app.browser.web.client.UI;

import com.google.gwt.resources.client.ImageResource;

public class SelectionInfo {
	public String title;
	public String firstLine;
	public String secondLine;
	public String date;
	public String onClickURL;
	public String imageURL;
	public ImageResource imageResource;
	
	public SelectionInfo(String title, String firstLine, String secondLine, String date, String onClickURL, String imageURL, ImageResource imageResource) {
		this.title = title;
		this.firstLine = firstLine;
		this.secondLine = secondLine;
		this.date = date;
		this.onClickURL = onClickURL;
		this.imageURL = imageURL;
		this.imageResource = imageResource;
	}
	
	public SelectionInfo(String title, String firstLine, String secondLine, String date, ImageResource imageResource) {
		this(title, firstLine, secondLine, date, null, null, imageResource);
	}
	
	public SelectionInfo(String title, String firstLine, String secondLine, String date, String onClickURL, String imageURL) {
		this(title, firstLine, secondLine, date, onClickURL, imageURL, null);
	}

}

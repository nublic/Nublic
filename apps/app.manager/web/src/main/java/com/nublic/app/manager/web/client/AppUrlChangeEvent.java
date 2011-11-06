package com.nublic.app.manager.web.client;

public class AppUrlChangeEvent {

	String url, title;
	
	public AppUrlChangeEvent(String url, String title) {
		this.url = url;
		this.title = title;
	}

	public String getUrl() {
		return this.url;
	}
	
	public String getTitle() {
		return this.title;
	}
}

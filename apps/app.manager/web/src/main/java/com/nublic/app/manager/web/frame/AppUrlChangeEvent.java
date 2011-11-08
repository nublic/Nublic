package com.nublic.app.manager.web.frame;

public class AppUrlChangeEvent {

	String url, title, hash;
	
	public AppUrlChangeEvent(String url, String title, String hash) {
		this.url = url;
		this.title = title;
		this.hash = hash;
	}

	public String getUrl() {
		return this.url;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getHash() {
		return this.hash;
	}
	
	public String getBaseUrl() {
		if (hash.isEmpty()) {
			return url;
		} else {
			return url.replace("#" + hash, "");
		}
	}
}

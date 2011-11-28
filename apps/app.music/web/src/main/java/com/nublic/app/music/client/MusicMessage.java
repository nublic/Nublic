package com.nublic.app.music.client;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.Message;

public class MusicMessage extends Message {
	String path = null;
	
	public MusicMessage(String path) {
		this.path = path;
	}
	
	@Override
	public String getURL() {
		return URL.encode(GWT.getHostPageBaseURL() + "server/" + path);
	}

	@Override
	public void onSuccess(Response response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError() {
		ErrorPopup.showError("Server unavailable");
	}

}

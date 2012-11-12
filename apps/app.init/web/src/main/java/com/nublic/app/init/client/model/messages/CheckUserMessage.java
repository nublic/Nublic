package com.nublic.app.init.client.model.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.init.client.Constants;
import com.nublic.app.init.client.model.handlers.CheckUserHandler;
import com.nublic.util.messages.Message;

public class CheckUserMessage extends Message {
	String name;
	CheckUserHandler cuh;
	
	public CheckUserMessage(String name, CheckUserHandler cuh) {
		this.name = name;
		this.cuh = cuh;
	}
	
	@Override
	public String getURL() {
		//addParam("name", name);
		return URL.encode(GWT.getHostPageBaseURL() + "server/checkuser/" + name);
	}

	@Override
	public void onSuccess(Response response) {
		if (response.getStatusCode() == Response.SC_OK) {
			String text = response.getText();

			if (text.compareTo(Constants.EXISTS) == 0) {
				cuh.onUserChecked(name, false);
			} else {
				cuh.onUserChecked(name, true);
			}
		} else {
			onError();
		}
	}

	@Override
	public void onError() {
//		ErrorPopup.showError(Constants.I18N.addPlaylistError());
	}

}

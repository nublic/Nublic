package com.nublic.app.init.client.model.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.init.client.Constants;
import com.nublic.app.init.client.model.handlers.CheckNublicNameHandler;
import com.nublic.util.messages.Message;

public class CheckNublicNameMessage extends Message {
	String name;
	CheckNublicNameHandler cnnh;
	
	public CheckNublicNameMessage(String name, CheckNublicNameHandler cnnh) {
		this.name = name;
		this.cnnh = cnnh;
	}
	
	@Override
	public String getURL() {
		//addParam("name", name);
		return URL.encode(GWT.getHostPageBaseURL() + "server/checknublicname/" + name);
	}

	@Override
	public void onSuccess(Response response) {
		if (response.getStatusCode() == Response.SC_OK) {
			String text = response.getText();

			if (text.compareTo(Constants.EXISTS) == 0) {
				cnnh.onNublicNameChecked(name, false);
			} else {
				cnnh.onNublicNameChecked(name, true);
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

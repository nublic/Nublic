package com.nublic.app.manager.settings.client;

import com.google.gwt.http.client.Response;
import com.nublic.util.messages.Message;

public class DeleteUserMessage extends Message {
	String systemName;
	String masterPass;
	DeleteUserHandler duh;

	public DeleteUserMessage(String systemName, String masterPass, DeleteUserHandler duh) {
		this.systemName = systemName;
		this.masterPass = masterPass;
		this.duh = duh;
	}

	@Override
	public String getURL() {
		addParam("name", systemName);
		addParam("pass", masterPass);
		return "/manager/server/deleteuser/";
	}

	@Override
	public void onSuccess(Response response) {
		if (response.getStatusCode() == Response.SC_OK) {
			if (response.getText().equals("ok")) {
				duh.onUserDeleted(true);
			} else {
				duh.onUserDeleted(false);
			}
		} else {
			onError();
		}
	}

	@Override
	public void onError() {
		duh.onUserDeleted(false);
	}

}

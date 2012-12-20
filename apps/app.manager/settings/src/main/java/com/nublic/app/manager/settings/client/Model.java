package com.nublic.app.manager.settings.client;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.nublic.app.manager.settings.client.comm.JSUser;
import com.nublic.app.manager.settings.client.comm.User;
import com.nublic.app.manager.settings.client.comm.UserMessageCallback;
import com.nublic.util.gwt.LocationUtil;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;

public class Model {
	
	public static Model INSTANCE;
	
	public static Model create() {
		if (INSTANCE == null) {
			INSTANCE = new Model();
		}
		return INSTANCE;
	}

	public void getUserInfo(final UserMessageCallback umc) {
		SequenceHelper.sendJustOne(new Message() {
			@Override
			public String getURL() {
				return LocationUtil.getHostBaseUrl() + "manager/server/user-info";
			}
			@Override
			public void onSuccess(Response response) {				
				if (response.getStatusCode() == Response.SC_OK) {
					// When the call is successful
					String text = response.getText();
					JSUser jsUser = JsonUtils.safeEval(text);
					umc.onUserMessage(new User(jsUser));
				}
			}
			@Override
			public void onError() {
				// Do nothing
			}
		}, RequestBuilder.GET);
		
	}

	public void setUserShownName(String newName) {
		Message m = new Message() {
			@Override
			public String getURL() {
				return LocationUtil.getHostBaseUrl() + "manager/server/user-info";
			}
			@Override
			public void onSuccess(Response response) {				
				// Do nothing
			}
			@Override
			public void onError() {
				// Do nothing
			}
		};
		m.addParam("name", newName);
		SequenceHelper.sendJustOne(m, RequestBuilder.PUT);
	}

	public boolean changePassword(String oldPass, String newPass) {
		Message m = new Message() {
			@Override
			public String getURL() {
				return LocationUtil.getHostBaseUrl() + "manager/server/change-password";
			}
			@Override
			public void onSuccess(Response response) {				
				// TODO: Do something
			}
			@Override
			public void onError() {
				// Do nothing
			}
		};
		m.addParam("old", oldPass);
		m.addParam("new", newPass);
		SequenceHelper.sendJustOne(m, RequestBuilder.POST);
		return true;
	}

}

package com.nublic.app.manager.web.client;

import com.google.gwt.http.client.Response;
import com.nublic.util.gwt.LocationUtil;
import com.nublic.util.messages.Message;

public class FavouriteMessage extends Message {

	private String appId;
	
	public FavouriteMessage(String appId) {
		this.appId = appId;
	}
	
	@Override
	public String getURL() {
		return LocationUtil.getHostBaseUrl() + "manager/server/favourite/" + appId;
	}

	@Override
	public void onSuccess(Response response) {
		// Do nothing
	}

	@Override
	public void onError() {
		// Do nothing
	}

}

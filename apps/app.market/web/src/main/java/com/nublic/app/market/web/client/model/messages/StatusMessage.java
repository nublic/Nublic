package com.nublic.app.market.web.client.model.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.market.web.client.model.AppStatus;
import com.nublic.app.market.web.client.model.handlers.InstallActionHandler;
import com.nublic.app.market.web.client.model.js.StatusJS;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;

public class StatusMessage extends Message {
	String appId;
	InstallActionHandler iah;

	public StatusMessage(String appId, InstallActionHandler iah) {
		this.appId = appId;
		this.iah = iah;
	}
	
	public static void sendStatusMessage(String appId, InstallActionHandler iah) {
		StatusMessage sm = new StatusMessage(appId, iah);
		SequenceHelper.sendJustOne(sm, RequestBuilder.GET);
	}

	@Override
	public String getURL() {
		return URL.encode(GWT.getHostPageBaseURL() + "server/status/" + appId);
	}

	@Override
	public void onSuccess(Response response) {
		if (response.getStatusCode() == Response.SC_OK) {
			String text = response.getText();
			StatusJS jsStatus = JsonUtils.safeEval(text);
			AppStatus status = AppStatus.parse(jsStatus.getStatus());
			iah.actionSuccessful(status);
		} else {
			onError();
		}
	}

	@Override
	public void onError() {
		// Errors in polling will not overflow screen with popups
	}
}
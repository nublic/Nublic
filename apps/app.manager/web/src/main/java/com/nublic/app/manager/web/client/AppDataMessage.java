package com.nublic.app.manager.web.client;

import java.util.HashMap;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.gwt.LocationUtil;
import com.nublic.util.messages.Message;

public class AppDataMessage extends Message {
	
	ManagerUi theUi;
	
	public AppDataMessage(ManagerUi theUi) {
		this.theUi = theUi;
	}

	@Override
	public String getURL() {
            return LocationUtil.getHostBaseUrl() + "manager/server/apps";
	}

	@Override
	public void onSuccess(Response response) {		
		if (Response.SC_OK == response.getStatusCode()) {
			// When the call is successful
			String text = response.getText();
			JsArray<WebData> data = JsonUtils.safeEval(text);
			HashMap<String, AppData> apps = new HashMap<String, AppData>();
			for (int i = 0; i < data.length(); i++) {
				WebData oneData = data.get(i);
				apps.put(oneData.getId(), new AppData(oneData));
			}
			theUi.loadApps(apps);
			
		} else {
			ErrorPopup.showError("App list could not be loaded");
		}
	}

	@Override
	public void onError() {
		ErrorPopup.showError("App list could not be loaded");
	}

}

package com.nublic.app.market.web.client.model.messages;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.market.web.client.Constants;
import com.nublic.app.market.web.client.model.AppInfo;
import com.nublic.app.market.web.client.model.handlers.AppListHandler;
import com.nublic.app.market.web.client.model.js.AppInfoJS;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.Message;


//GET /packages
//* Return: [package1, package2, ...]
public class AskForAppListMessage extends Message {
	AppListHandler alh;

	public AskForAppListMessage(AppListHandler alh) {
		this.alh = alh;
	}

	@Override
	public String getURL() {
		return URL.encode(GWT.getHostPageBaseURL() + "server/packages");
	}

	@Override
	public void onSuccess(Response response) {
		 JsArray<AppInfoJS> jsInfoList = null;
		
		if (response.getStatusCode() == Response.SC_OK) {
			String text = response.getText();
			jsInfoList = JsonUtils.safeEval(text);
			
			if (jsInfoList == null) {
				onError();
			} else {
				Map<String, AppInfo> appMap = new HashMap<String, AppInfo>();
				for (int i = 0; i < jsInfoList.length(); i++) {
					AppInfo app = new AppInfo(jsInfoList.get(i)); 
					appMap.put(app.getId(), app);
				}
				alh.onAppListReceived(appMap);
			}
		} else {
			onError();
		}
	}

	@Override
	public void onError() {
		ErrorPopup.showError(Constants.I18N.errorGetAppList());
	}

}

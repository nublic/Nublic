package com.nublic.app.market.web.client.model.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.market.web.client.Constants;
import com.nublic.app.market.web.client.model.AppInfo;
import com.nublic.app.market.web.client.model.handlers.AppReceivedHandler;
import com.nublic.app.market.web.client.model.js.AppInfoJS;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;

public class PackageMessage extends Message {
	String appId;
	AppReceivedHandler arh;

	public PackageMessage(String appId, AppReceivedHandler arh) {
		this.appId = appId;
		this.arh = arh;
	}
	
	public static void sendPackageMessage(String appId, AppReceivedHandler arh) {
		PackageMessage sm = new PackageMessage(appId, arh);
		SequenceHelper.sendJustOne(sm, RequestBuilder.GET);
	}

	@Override
	public String getURL() {
		return URL.encode(GWT.getHostPageBaseURL() + "server/package/" + appId);
	}

	@Override
	public void onSuccess(Response response) {
		if (response.getStatusCode() == Response.SC_OK) {
			String text = response.getText();
			AppInfoJS jsApp = JsonUtils.safeEval(text);
			AppInfo app = new AppInfo(jsApp);
			arh.onAppReceived(app);
		} else {
			onError();
		}
	}

	@Override
	public void onError() {
		ErrorPopup.showError(Constants.I18N.errorPackage());
	}
}

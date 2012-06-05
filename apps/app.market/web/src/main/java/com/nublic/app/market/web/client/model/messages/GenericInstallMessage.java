package com.nublic.app.market.web.client.model.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.market.web.client.Constants;
import com.nublic.app.market.web.client.model.AppStatus;
import com.nublic.app.market.web.client.model.handlers.InstallActionHandler;
import com.nublic.app.market.web.client.model.js.StatusJS;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;


// PUT /packages
//  package = actual_id
// DELETE /packages/id
//  package = actual_id
public class GenericInstallMessage extends Message {
	String appId;
	InstallActionHandler iah;
	String errorMessage;

	public GenericInstallMessage(String appId, InstallActionHandler iah, String errorMessage) {
		this.appId = appId;
		this.iah = iah;
		this.errorMessage = errorMessage;
	}

	public static void sendInstallMessage(String appId, InstallActionHandler iah) {
		GenericInstallMessage gim = new GenericInstallMessage(appId, iah, Constants.I18N.errorCouldNotInstall());
		SequenceHelper.sendJustOne(gim, RequestBuilder.PUT);
	}

	public static void sendUninstallMessage(String appId, InstallActionHandler iah) {
		GenericInstallMessage gim = new GenericInstallMessage(appId, iah, Constants.I18N.errorCouldNotUninstall());
		SequenceHelper.sendJustOne(gim, RequestBuilder.DELETE);
	}

	@Override
	public String getURL() {
		this.addParam("package", appId);
		return URL.encode(GWT.getHostPageBaseURL() + "server/packages");
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
		ErrorPopup.showError(errorMessage);
	}
}

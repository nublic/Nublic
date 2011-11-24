package com.nublic.app.browser.web.client.UI.actions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.UI.FileWidget;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;

public class SetDownloadAction extends ActionWidget {

	public SetDownloadAction(BrowserUi stateProvider) {
		super("images/download.png", "Download selected files", stateProvider);
	}

	@Override
	public void executeAction() {
		Message m = new Message() {
			@Override
			public void onSuccess(Response response) {}
			@Override
			public void onError() {}
			@Override
			public String getURL() {
				return URL.encode(GWT.getHostPageBaseURL() + "server/zip-set");
			}
		};
		StringBuilder setOfFiles = new StringBuilder();
		for (Widget w : stateProvider.getSelectedFiles()) {
			if (setOfFiles.length() != 0) {
				setOfFiles.append(":");
			}
			setOfFiles.append(((FileWidget) w).getPath());
		}
		m.addParam("files", setOfFiles.toString());
		SequenceHelper.sendJustOne(m, RequestBuilder.POST);
	}

	@Override
	public Availability getAvailability() {
		if (stateProvider.getSelectedFiles().size() < 2) {
			return Availability.HIDDEN;
		} else {
			return Availability.AVAILABLE;
		}
	}

}

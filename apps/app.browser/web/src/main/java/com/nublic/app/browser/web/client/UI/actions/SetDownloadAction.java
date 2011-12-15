package com.nublic.app.browser.web.client.UI.actions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.UI.FileWidget;
import com.nublic.util.messages.PostRedirectHelper;

public class SetDownloadAction extends ActionWidget {

	public SetDownloadAction(BrowserUi stateProvider) {
		super("images/download.png", "Download selected files", stateProvider);
	}

	@Override
	public void executeAction() {
		StringBuilder setOfFiles = new StringBuilder();
		for (Widget w : stateProvider.getSelectedFiles()) {
			if (setOfFiles.length() != 0) {
				setOfFiles.append(":");
			}
			setOfFiles.append(((FileWidget) w).getPath());
		}
		String path = stateProvider.getPath();

		PostRedirectHelper postSender = new PostRedirectHelper(GWT.getHostPageBaseURL() + "server/zip-set");
		postSender.addParam("files", setOfFiles.toString());
		if (path.contains("/")) {
			postSender.addParam("filename", path.substring(path.lastIndexOf("/") + 1) + "." + Constants.COMPRESSED_TYPE);
		} else {
			postSender.addParam("filename", path + "." + Constants.COMPRESSED_TYPE);
		}
		
		postSender.send();
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
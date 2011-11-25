package com.nublic.app.browser.web.client.UI.actions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.UI.FileWidget;

public class SingleDownloadAction extends ActionWidget {

	public static void download(String path) {
		Window.open(GWT.getHostPageBaseURL() + "server/raw/" + path, "_self", "");
		//Window.Location.assign(GWT.getHostPageBaseURL() + "server/raw/" + path);
	}

	public SingleDownloadAction(BrowserUi stateProvider) {
		super("images/download.png", "Download this file", stateProvider);
	}

	@Override
	public void executeAction() {
		// Redirect navigation to raw resource in server
		//Window.open(GWT.getHostPageBaseURL() + "server/view/" + path + "." + Constants.IMAGE_TYPE, "_self", "");
		for (Widget w : stateProvider.getSelectedFiles()) {
			download(((FileWidget)w).getPath());
		}
	}

	@Override
	public Availability getAvailability() {
//		if (stateProvider.getPath()) {
//			// TODO: some condition on folders which content cannot be downloadable	
//		} else {
			if (stateProvider.getSelectedFiles().size() == 1) {
				return Availability.AVAILABLE;
			} else {
				return Availability.HIDDEN;
			}
//		}
	}
}

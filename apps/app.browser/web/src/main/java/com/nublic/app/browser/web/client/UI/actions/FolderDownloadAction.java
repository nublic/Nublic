package com.nublic.app.browser.web.client.UI.actions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.nublic.app.browser.web.client.UI.BrowserUi;

public class FolderDownloadAction extends ActionWidget {

	public FolderDownloadAction(BrowserUi stateProvider) {
		super("images/folder_download.png", "Download folder as zip", stateProvider);
	}

	@Override
	public void executeAction() {
//		String realPath = stateProvider.getDevicesManager().getRealPath(stateProvider.getPath());
//		Window.open(GWT.getHostPageBaseURL() + "server/zip/" + realPath, "_self", "");
		Window.open(GWT.getHostPageBaseURL() + "server/zip/" + stateProvider.getShowingPath(), "_self", "");
	}

	@Override
	public Availability getAvailability() {
		if (stateProvider.getSelectedFiles().isEmpty()) {
			return Availability.AVAILABLE;
		} else {
			return Availability.HIDDEN;
		}
	}

}

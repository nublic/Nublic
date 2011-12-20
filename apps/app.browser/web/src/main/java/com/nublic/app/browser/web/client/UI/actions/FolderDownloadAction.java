package com.nublic.app.browser.web.client.UI.actions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.nublic.app.browser.web.client.UI.BrowserUi;

public class FolderDownloadAction extends ActionWidget {

	public FolderDownloadAction(BrowserUi stateProvider) {
		super("images/folder_download.png", "Download the whole folder", stateProvider);
	}

	@Override
	public void executeAction() {
//		String realPath = stateProvider.getDevicesManager().getRealPath(stateProvider.getPath());
//		Window.open(GWT.getHostPageBaseURL() + "server/zip/" + realPath, "_self", "");
		Window.open(GWT.getHostPageBaseURL() + "server/zip/" + stateProvider.getPath(), "_self", "");
	}

	@Override
	public Availability getAvailability() {
//		if (stateProvider.getPath()) {
//			// TODO: some condition on folders which content cannot be downloadable	
//		} else {
			return Availability.AVAILABLE;
//		}
	}

}

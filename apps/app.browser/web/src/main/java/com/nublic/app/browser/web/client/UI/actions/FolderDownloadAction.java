package com.nublic.app.browser.web.client.UI.actions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.Resources;
import com.nublic.app.browser.web.client.UI.BrowserUi;

public class FolderDownloadAction extends ActionWidget {

	public FolderDownloadAction(BrowserUi stateProvider) {
		super(Resources.INSTANCE.folderDownload(), Constants.I18N.dowloadFolder(), stateProvider);
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

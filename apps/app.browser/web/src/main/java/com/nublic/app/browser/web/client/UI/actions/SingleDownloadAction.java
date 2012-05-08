package com.nublic.app.browser.web.client.UI.actions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.Resources;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.UI.FileWidget;

public class SingleDownloadAction extends ActionWidget {

	public static void download(String path, boolean isFolder) {
		if (isFolder) {
			Window.open(GWT.getHostPageBaseURL() + "server/zip/" + path, "_self", "");
		} else {
			Window.open(GWT.getHostPageBaseURL() + "server/raw/" + path, "_self", "");
		}
		//Window.Location.assign(GWT.getHostPageBaseURL() + "server/raw/" + path);
	}

	public SingleDownloadAction(BrowserUi stateProvider) {
		super(Resources.INSTANCE.download(), Constants.I18N.downloadFile(), stateProvider);
	}

	@Override
	public void executeAction() {
		// Redirect navigation to raw resource in server
		for (Widget w : stateProvider.getSelectedFiles()) {
//			String realPath = stateProvider.getDevicesManager().getRealPath(((FileWidget) w).getPath());
//			download(realPath);
//			download(((FileWidget)w).getRealPath());
			FileWidget fw = (FileWidget)w;
			download(fw.getPath(), fw.isFolder());
		}
	}

	@Override
	public Availability getAvailability() {
		if (stateProvider.getSelectedFiles().size() == 1) {
			return Availability.AVAILABLE;
		} else {
			return Availability.HIDDEN;
		}
	}
}

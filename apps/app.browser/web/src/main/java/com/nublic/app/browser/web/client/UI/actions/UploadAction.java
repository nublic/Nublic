package com.nublic.app.browser.web.client.UI.actions;

import com.nublic.app.browser.web.client.Resources;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.model.FolderNode;

public class UploadAction extends ActionWidget {

	public UploadAction(BrowserUi stateProvider) {
		super(Resources.INSTANCE.addfile(), "Upload file", stateProvider);
	}
	
	public static void doUpload(String pathTo) {
		
	}

	@Override
	public void executeAction() {
		doUpload(stateProvider.getShowingPath());
	}

	@Override
	public Availability getAvailability() {
		if (stateProvider.getSelectedFiles().isEmpty()) {
			FolderNode n = stateProvider.getShowingFolder();
			if (n != null && n.isWritable()) {
				return Availability.AVAILABLE;
			} else {
				return Availability.UNCLICKABLE;
			}
		} else {
			return Availability.HIDDEN;
		}
	}

}

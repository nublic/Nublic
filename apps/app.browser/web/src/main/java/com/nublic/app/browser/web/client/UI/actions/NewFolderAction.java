package com.nublic.app.browser.web.client.UI.actions;

import com.nublic.app.browser.web.client.Resources;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.model.FolderNode;

public class NewFolderAction extends ActionWidget {
	
	public NewFolderAction(BrowserUi stateProvider) {
		super(Resources.INSTANCE.newfolder(), "Create new folder", stateProvider);
	}
	
	public static void doCreateFolder(String pathTo) {
		
	}

	@Override
	public void executeAction() {
		doCreateFolder(stateProvider.getShowingPath());
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

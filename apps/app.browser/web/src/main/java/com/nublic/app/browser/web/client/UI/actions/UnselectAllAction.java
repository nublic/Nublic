package com.nublic.app.browser.web.client.UI.actions;

import com.nublic.app.browser.web.client.UI.BrowserUi;

public class UnselectAllAction extends ActionWidget {

	public UnselectAllAction(BrowserUi stateProvider) {
		super("images/unselect_all.png", "Unselect all", stateProvider);
	}

	@Override
	public void executeAction() {
		stateProvider.unselectAllFiles();
	}

	@Override
	public Availability getAvailability() {
		if (stateProvider.getSelectedFiles().isEmpty()) {
			return Availability.HIDDEN;
		} else {
			return Availability.AVAILABLE;
		}
	}

}
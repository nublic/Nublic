package com.nublic.app.browser.web.client.UI.actions;

import com.nublic.app.browser.web.client.UI.BrowserUi;

public class SelectAllAction extends ActionWidget {

	public SelectAllAction(BrowserUi stateProvider) {
		super("images/select_all.png", "Select all", stateProvider);
	}

	@Override
	public void executeAction() {
		stateProvider.selectAllFiles();
	}

	@Override
	public Availability getAvailability() {
		if (stateProvider.getSelectedFiles().size() == stateProvider.getShowingFiles().size()) {
			return Availability.HIDDEN;
		} else {
			return Availability.AVAILABLE;
		}
	}

}

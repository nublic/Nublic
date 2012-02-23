package com.nublic.app.browser.web.client.UI.actions;

import com.nublic.app.browser.web.client.Resources;
import com.nublic.app.browser.web.client.UI.BrowserUi;

public class UnselectAllAction extends ActionWidget {

	public UnselectAllAction(BrowserUi stateProvider) {
		super(Resources.INSTANCE.unselectAll(), "Unselect all", stateProvider);
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
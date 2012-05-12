package com.nublic.app.browser.web.client.UI.actions;

import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.Resources;
import com.nublic.app.browser.web.client.UI.BrowserUi;

public class UnselectAllAction extends ActionWidget {

	public UnselectAllAction(BrowserUi stateProvider) {
		super(Resources.INSTANCE.unselectAll(), Constants.I18N.unselectAll(), stateProvider);
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
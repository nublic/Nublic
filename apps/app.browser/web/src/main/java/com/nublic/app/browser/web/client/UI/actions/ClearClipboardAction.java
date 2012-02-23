package com.nublic.app.browser.web.client.UI.actions;

import com.nublic.app.browser.web.client.Resources;
import com.nublic.app.browser.web.client.UI.BrowserUi;

public class ClearClipboardAction extends ActionWidget {

	public ClearClipboardAction(BrowserUi stateProvider) {
		super(Resources.INSTANCE.editClear(), "Clear clipboard", stateProvider);
	}

	@Override
	public void executeAction() {
		stateProvider.clearClipboard();
	}

	@Override
	public Availability getAvailability() {
		if (stateProvider.getClipboard().isEmpty()) {
			return Availability.HIDDEN;
		} else {
			return Availability.AVAILABLE;
		}
	}

}

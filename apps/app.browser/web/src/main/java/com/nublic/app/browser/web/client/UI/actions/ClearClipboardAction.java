package com.nublic.app.browser.web.client.UI.actions;

import com.nublic.app.browser.web.client.UI.BrowserUi;

public class ClearClipboardAction extends ActionWidget {

	public ClearClipboardAction(BrowserUi stateProvider) {
		super("images/edit_clear.png", "Clear clipboard", stateProvider);
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

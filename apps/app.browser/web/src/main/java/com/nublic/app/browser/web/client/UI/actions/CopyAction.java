package com.nublic.app.browser.web.client.UI.actions;

import java.util.Set;

import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.UI.BrowserUi;


public class CopyAction extends ActionWidget {

	public CopyAction(BrowserUi stateProvider) {
		super("images/edit_copy.png", "Copy", stateProvider);
	}

	@Override
	public void executeAction() {
		stateProvider.copy(stateProvider.getSelectedFiles());
	}

	@Override
	public Availability getAvailability() {
		// TODO: not permit copy depending on the place.. ? (ex: don't allow to copy a whole computer)
		Set<Widget> selected = stateProvider.getSelectedFiles();
		if (selected.isEmpty()) {
			setExtraInfo(null);
			return Availability.UNCLICKABLE;
		} else {
			// To give feedback on the number of selected files to copy
			setExtraInfo(String.valueOf(selected.size()));
			return Availability.AVAILABLE;
		}
	}

}

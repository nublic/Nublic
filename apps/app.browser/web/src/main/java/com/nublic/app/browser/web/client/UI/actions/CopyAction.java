package com.nublic.app.browser.web.client.UI.actions;

import java.util.Set;

import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.Resources;
import com.nublic.app.browser.web.client.UI.BrowserUi;


public class CopyAction extends ActionWidget {

	public CopyAction(BrowserUi stateProvider) {
		super(Resources.INSTANCE.editCopy(), "Copy", stateProvider);
	}

	@Override
	public void executeAction() {
		stateProvider.copy(stateProvider.getSelectedFiles());
	}

	@Override
	public Availability getAvailability() {
		Set<Widget> selected = stateProvider.getSelectedFiles();
		if (selected.isEmpty()) {
			setExtraInfo(null);
			return Availability.HIDDEN;
		} else {
			// To give feedback on the number of selected files to copy
//			setExtraInfo(String.valueOf(selected.size()));
			return Availability.AVAILABLE;
		}
	}

}

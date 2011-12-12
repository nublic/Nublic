package com.nublic.app.browser.web.client.UI.actions;

import java.util.Set;

import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.UI.FileWidget;

public class CutAction extends ActionWidget {

	public CutAction(BrowserUi stateProvider) {
		super("images/edit_cut.png", "Cut", stateProvider);
	}

	@Override
	public void executeAction() {
		stateProvider.cut(stateProvider.getSelectedFiles());
	}

	@Override
	public Availability getAvailability() {
		Set<Widget> selected = stateProvider.getSelectedFiles();
		
		// Check if any of the selected files is not writable (we won't allow cut in that case)
		for (Widget w : selected) {
			if (!((FileWidget)w).isWritable()) {
				return Availability.HIDDEN;
			}
		}
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

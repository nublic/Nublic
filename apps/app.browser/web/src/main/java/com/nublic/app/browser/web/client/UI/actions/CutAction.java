package com.nublic.app.browser.web.client.UI.actions;

import java.util.Set;

import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.Resources;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.UI.FileWidget;

public class CutAction extends ActionWidget {

	public CutAction(BrowserUi stateProvider) {
		super(Resources.INSTANCE.editCut(), Constants.I18N.cut(), stateProvider);
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
				return Availability.UNCLICKABLE;
			}
		}
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

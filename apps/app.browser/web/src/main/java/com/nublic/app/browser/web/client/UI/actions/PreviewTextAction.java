package com.nublic.app.browser.web.client.UI.actions;

import java.util.Set;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.Resources;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.UI.FileWidget;

public class PreviewTextAction extends ActionWidget {
	FileWidget textToShow = null;

	public PreviewTextAction(BrowserUi stateProvider) {
		super(Resources.INSTANCE.textView(), "Preview plain text", stateProvider);
	}

	@Override
	public void executeAction() {
		if (textToShow != null) {
			History.newItem(Constants.TEXT_VIEW + "?" + Constants.PATH_PARAMETER + "=" + textToShow.getPath());
		}
	}

	@Override
	public Availability getAvailability() {
		Set<Widget> selectedFiles = stateProvider.getSelectedFiles();
		if (selectedFiles.size() != 1) {
			textToShow = null;
			return Availability.HIDDEN;
		} else {
			for (Widget w : selectedFiles) {
				if (Constants.TEXT_TYPE.equals(((FileWidget)w).getViewType())) {
					textToShow = (FileWidget) w;
					return Availability.AVAILABLE;
				}
			}
			textToShow = null;
			return Availability.HIDDEN;
		}
	}

}

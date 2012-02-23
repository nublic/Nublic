package com.nublic.app.browser.web.client.UI.actions;

import java.util.Set;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.Resources;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.UI.FileWidget;

public class PreviewImageAction extends ActionWidget {
	FileWidget imageToShow = null;

	public PreviewImageAction(BrowserUi stateProvider) {
		super(Resources.INSTANCE.imageView(), "Preview image", stateProvider);
	}

	@Override
	public void executeAction() {
		if (imageToShow != null) {
			History.newItem(Constants.IMAGE_VIEW + "?" + Constants.PATH_PARAMETER + "=" + imageToShow.getPath());
		}
	}

	@Override
	public Availability getAvailability() {
		Set<Widget> selectedFiles = stateProvider.getSelectedFiles();
		if (selectedFiles.size() != 1) {
			imageToShow = null;
			return Availability.HIDDEN;
		} else {
			for (Widget w : selectedFiles) {
				if (Constants.IMAGE_TYPE.equals(((FileWidget)w).getViewType())) {
					imageToShow = (FileWidget) w;
					return Availability.AVAILABLE;
				}
			}
			imageToShow = null;
			return Availability.HIDDEN;
		}
	}

}

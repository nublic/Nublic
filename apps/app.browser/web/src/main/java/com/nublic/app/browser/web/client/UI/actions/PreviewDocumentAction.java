package com.nublic.app.browser.web.client.UI.actions;

import java.util.Set;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.Resources;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.UI.FileWidget;

public class PreviewDocumentAction extends ActionWidget {
	FileWidget docToShow = null;

	public PreviewDocumentAction(BrowserUi stateProvider) {
		super(Resources.INSTANCE.documentView(), "Preview document", stateProvider);
	}

	@Override
	public void executeAction() {
		if (docToShow != null) {
			History.newItem(Constants.DOCUMENT_VIEW + "?" + Constants.PATH_PARAMETER + "=" + docToShow.getPath());
		}
	}

	@Override
	public Availability getAvailability() {
		Set<Widget> selectedFiles = stateProvider.getSelectedFiles();
		if (selectedFiles.size() != 1) {
			docToShow = null;
			return Availability.HIDDEN;
		} else {
			for (Widget w : selectedFiles) {
				if (Constants.DOCUMENT_TYPE.equals(((FileWidget)w).getViewType())) {
					docToShow = (FileWidget) w;
					return Availability.AVAILABLE;
				}
			}
			docToShow = null;
			return Availability.HIDDEN;
		}
	}

}

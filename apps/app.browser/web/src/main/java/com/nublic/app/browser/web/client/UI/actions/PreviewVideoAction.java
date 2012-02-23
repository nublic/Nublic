package com.nublic.app.browser.web.client.UI.actions;

import java.util.Set;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.Resources;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.UI.FileWidget;

public class PreviewVideoAction extends ActionWidget {
	FileWidget videoToShow = null;

	public PreviewVideoAction(BrowserUi stateProvider) {
		super(Resources.INSTANCE.videoView(), "Preview video", stateProvider);
	}

	@Override
	public void executeAction() {
		if (videoToShow != null) {
			History.newItem(Constants.VIDEO_VIEW + "?" + Constants.PATH_PARAMETER + "=" + videoToShow.getPath());
		}
	}

	@Override
	public Availability getAvailability() {
		Set<Widget> selectedFiles = stateProvider.getSelectedFiles();
		if (selectedFiles.size() != 1) {
			videoToShow = null;
			return Availability.HIDDEN;
		} else {
			for (Widget w : selectedFiles) {
				if (Constants.VIDEO_TYPE.equals(((FileWidget)w).getViewType())) {
					videoToShow = (FileWidget) w;
					return Availability.AVAILABLE;
				}
			}
			videoToShow = null;
			return Availability.HIDDEN;
		}
	}

}


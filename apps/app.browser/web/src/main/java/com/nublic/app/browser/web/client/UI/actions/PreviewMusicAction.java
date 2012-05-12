package com.nublic.app.browser.web.client.UI.actions;

import java.util.Set;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.Resources;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.UI.FileWidget;

public class PreviewMusicAction extends ActionWidget {
	FileWidget musicToShow = null;

	public PreviewMusicAction(BrowserUi stateProvider) {
		super(Resources.INSTANCE.musicView(), Constants.I18N.previewMusic(), stateProvider);
	}

	@Override
	public void executeAction() {
		if (musicToShow != null) {
			History.newItem(Constants.MUSIC_VIEW + "?" + Constants.PATH_PARAMETER + "=" + musicToShow.getPath());
		}
	}

	@Override
	public Availability getAvailability() {
		Set<Widget> selectedFiles = stateProvider.getSelectedFiles();
		if (selectedFiles.size() != 1) {
			musicToShow = null;
			return Availability.HIDDEN;
		} else {
			for (Widget w : selectedFiles) {
				if (Constants.MUSIC_TYPE.equals(((FileWidget)w).getViewType())) {
					musicToShow = (FileWidget) w;
					return Availability.AVAILABLE;
				}
			}
			musicToShow = null;
			return Availability.HIDDEN;
		}
	}

}

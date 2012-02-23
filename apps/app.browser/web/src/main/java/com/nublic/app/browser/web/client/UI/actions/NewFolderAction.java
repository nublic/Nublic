package com.nublic.app.browser.web.client.UI.actions;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.Resources;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.model.FolderNode;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;

public class NewFolderAction extends ActionWidget {
	
	public NewFolderAction(BrowserUi stateProvider) {
		super(Resources.INSTANCE.newfolder(), "Create new folder", stateProvider);
	}
	
	public static void doCreateFolder(final String newFolderName, String pathTo, final BrowserUi feedbackTarget) {
		Message m = new Message() {
			@Override
			public String getURL() {
				return URL.encode(GWT.getHostPageBaseURL() + "server/new-folder");
			}
			@Override
			public void onSuccess(Response response) {
				if (response.getStatusCode() == Response.SC_OK) {
					feedbackTarget.getModel().addFile(newFolderName, Constants.FOLDER_MIME, null, 0, new Date().getTime(), true, false);
					feedbackTarget.getModel().fireFilesUpdateHandlers(false, false);
				} else {
					ErrorPopup.showError("Could not create folder");
				}
			}
			@Override
			public void onError() {
				ErrorPopup.showError("Could not create folder");
			}
		};
		m.addParam("name", newFolderName);
		m.addParam("path", pathTo);
		SequenceHelper.sendJustOne(m, RequestBuilder.POST);
	}

	@Override
	public void executeAction() {
		stateProvider.showNewFolderPopup();
	}

	@Override
	public Availability getAvailability() {
		if (stateProvider.getSelectedFiles().isEmpty()) {
			FolderNode n = stateProvider.getShowingFolder();
			if (n != null && n.isWritable()) {
				return Availability.AVAILABLE;
			} else {
				return Availability.UNCLICKABLE;
			}
		} else {
			return Availability.HIDDEN;
		}
	}

}

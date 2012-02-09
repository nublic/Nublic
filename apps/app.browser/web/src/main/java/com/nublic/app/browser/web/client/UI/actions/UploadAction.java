package com.nublic.app.browser.web.client.UI.actions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.FileUpload;
import com.nublic.app.browser.web.client.Resources;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.model.FolderNode;
import com.nublic.util.messages.PostRedirectHelper;

public class UploadAction extends ActionWidget {

	public UploadAction(BrowserUi stateProvider) {
		super(Resources.INSTANCE.addfile(), "Upload file", stateProvider);
	}
	
	public static void doUpload(String pathTo, FileUpload uploadWidget) {
		PostRedirectHelper sendFileHelper = new PostRedirectHelper(URL.encode(GWT.getHostPageBaseURL() + "server/upload"));
		sendFileHelper.addParam("path", pathTo);
		int backSlashIndex = uploadWidget.getFilename().lastIndexOf("\\");
		String fileName = uploadWidget.getFilename();
		if (backSlashIndex >= 0) {
			fileName = fileName.substring(backSlashIndex + 1); 
		}
		sendFileHelper.addParam("name", fileName);
		sendFileHelper.addParam("contents", uploadWidget);
		
		sendFileHelper.send();
	}

	@Override
	public void executeAction() {
		stateProvider.showUploadPopup();
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

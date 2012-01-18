package com.nublic.app.browser.web.client.UI.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.UI.FileWidget;
import com.nublic.app.browser.web.client.model.FileNode;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;

public class DeleteAction extends ActionWidget {

	public DeleteAction(BrowserUi stateProvider) {
		super("images/edit_delete.png", "Delete", stateProvider);
	}

	@Override
	public void executeAction() {
		// final variables to check and give feedback on response
		final String pathFrom = stateProvider.getShowingPath();
		final List<FileNode> removedFiles = new ArrayList<FileNode>();
		// Create the parameters to the request
		StringBuilder setOfFiles = new StringBuilder();
		for (Widget w : stateProvider.getSelectedFiles()) {
			FileWidget fw = (FileWidget) w;
			if (setOfFiles.length() != 0) {
				setOfFiles.append(":");
			}
			setOfFiles.append(fw.getPath());
			removedFiles.add(fw.getNode());
		}
		
		Message m = new Message() {
			@Override
			public void onSuccess(Response response) {
				if (response.getStatusCode() == Response.SC_OK) {
					if (pathFrom.equals(stateProvider.getShowingPath())) {
						stateProvider.getModel().removeFiles(removedFiles);
					}
				} else {
					ErrorPopup.showError("Could not delete files");
				}
			}
			@Override
			public void onError() {
				ErrorPopup.showError("Could not delete files");
			}
			@Override
			public String getURL() {
				return URL.encode(GWT.getHostPageBaseURL() + "server/delete");
			}
		};

		m.addParam("files", setOfFiles.toString());
		SequenceHelper.sendJustOne(m, RequestBuilder.POST);
	}

	@Override
	public Availability getAvailability() {
		Set<Widget> selected = stateProvider.getSelectedFiles();
		
		// Check if any of the selected files is not writable (we won't allow cut in that case)
		for (Widget w : selected) {
			if (!((FileWidget) w).isWritable()) {
				return Availability.UNCLICKABLE;
			}
		}

		if (selected.isEmpty()) {
			setExtraInfo(null);
			return Availability.HIDDEN;
		} else {
			// To give feedback on the number of selected files to delete
//			setExtraInfo(String.valueOf(selected.size()));
			return Availability.AVAILABLE;
		}
	}

}

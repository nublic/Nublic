package com.nublic.app.browser.web.client.UI.actions;

import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.UI.FileWidget;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;

public class DeleteAction extends ActionWidget {

	public DeleteAction(BrowserUi stateProvider) {
		super("images/edit_delete.png", "Delete", stateProvider);
	}

	@Override
	public void executeAction() {
		Message m = new Message() {
			@Override
			public void onSuccess(Response response) {} // TODO: feedback
			@Override
			public void onError() {} // TODO: feedback
			@Override
			public String getURL() {
				return URL.encode(GWT.getHostPageBaseURL() + "server/delete");
			}
		};
		StringBuilder setOfFiles = new StringBuilder();
		for (Widget w : stateProvider.getSelectedFiles()) {
			if (setOfFiles.length() != 0) {
				setOfFiles.append(":");
			}
//			String realPath = stateProvider.getDevicesManager().getRealPath(((FileWidget) w).getPath());
//			setOfFiles.append(realPath);
			setOfFiles.append(((FileWidget) w).getRealPath());
//			setOfFiles.append(((FileWidget) w).getPath());
		}
		m.addParam("files", setOfFiles.toString());
		SequenceHelper.sendJustOne(m, RequestBuilder.POST);
	}

	@Override
	public Availability getAvailability() {
		Set<Widget> selected = stateProvider.getSelectedFiles();
		
		// Check if any of the selected files is not writable (we won't allow cut in that case)
		for (Widget w : selected) {
			if (!((FileWidget) w).isWritable()) {
				return Availability.HIDDEN;
			}
		}

		if (selected.isEmpty()) {
			setExtraInfo(null);
			return Availability.UNCLICKABLE;
		} else {
			// To give feedback on the number of selected files to delete
			setExtraInfo(String.valueOf(selected.size()));
			return Availability.AVAILABLE;
		}
	}

}

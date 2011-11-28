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

public class PasteAction extends ActionWidget {

	public PasteAction(BrowserUi stateProvider) {
		super("images/edit_paste.png", "Paste", stateProvider);
	}

	@Override
	public void executeAction() {
		final String mode = stateProvider.getModeCut() ? "move" : "copy";
		Message m = new Message() {
			@Override
			public void onSuccess(Response response) {} // TODO: feedback
			@Override
			public void onError() {} // TODO: feedback
			@Override
			public String getURL() {
				return URL.encode(GWT.getHostPageBaseURL() + "server/" + mode);
			}
		};
		StringBuilder setOfFiles = new StringBuilder();
		for (Widget w : stateProvider.getClipboard()) {
			if (setOfFiles.length() != 0) {
				setOfFiles.append(":");
			}
			setOfFiles.append(((FileWidget) w).getPath());
		}
		m.addParam("files", setOfFiles.toString());
		m.addParam("target", stateProvider.getPath());
		SequenceHelper.sendJustOne(m, RequestBuilder.POST);
		
		// TODO: if mode cut remove filewidgets from view
	}

	@Override
	public Availability getAvailability() {
		// TODO: not allow to paste in certain locations
		Set<Widget> clipboard = stateProvider.getClipboard();
		if (clipboard.isEmpty()) {
			setExtraInfo(null);
			return Availability.UNCLICKABLE;
		} else {
			// To give feedback on the number of selected files to paste
			setExtraInfo(String.valueOf(clipboard.size()));
			return Availability.AVAILABLE;
		}
	}

}

package com.nublic.app.browser.web.client.UI.actions;

import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.UI.FileWidget;
import com.nublic.app.browser.web.client.model.FolderNode;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;

public class PasteAction extends ActionWidget {
	
	public PasteAction(BrowserUi stateProvider) {
		super("images/edit_paste.png", "Paste", stateProvider);
	}
	
	public static void doPasteAction(final String mode, Set<Widget> setToCopy, String pathTo) {
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
		for (Widget w : setToCopy) {
			if (setOfFiles.length() != 0) {
				setOfFiles.append(":");
			}
			setOfFiles.append(((FileWidget) w).getPath());
		}
		m.addParam("files", setOfFiles.toString());
		m.addParam("target", pathTo);
		SequenceHelper.sendJustOne(m, RequestBuilder.POST);
	}

	@Override
	public void executeAction() {
		doPasteAction(stateProvider.getModeCut() ? "move" : "copy",
				      stateProvider.getClipboard(),
				      stateProvider.getPath());
		// TODO: if mode cut remove filewidgets from view
	}

	@Override
	public Availability getAvailability() {
		FolderNode n = stateProvider.getShowingFolder();
		if (n != null && n.isWritable()) {
			Set<Widget> clipboard = stateProvider.getClipboard();
			if (clipboard.isEmpty()) {
				setExtraInfo(null);
				return Availability.UNCLICKABLE;
			} else {
				// To give feedback on the number of selected files to paste
				setExtraInfo(String.valueOf(clipboard.size()));
				return Availability.AVAILABLE;
			}
		} else {
			return Availability.HIDDEN;
		}
	}

}

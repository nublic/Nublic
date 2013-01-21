package com.nublic.app.browser.web.client.UI.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.Resources;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.UI.FileWidget;
import com.nublic.app.browser.web.client.model.BrowserModel;
import com.nublic.app.browser.web.client.model.FileNode;
import com.nublic.app.browser.web.client.model.FolderNode;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;

public class PasteAction extends ActionWidget {
	
	public PasteAction(BrowserUi stateProvider) {
		super(Resources.INSTANCE.editPaste(), Constants.I18N.paste(), stateProvider);
	}

	public static void doPasteAction(final String mode, final Set<Widget> setToCopy, final String pathTo, final BrowserModel feedbackTarget,
			final BrowserUi stateProvider) {
		String tempPathFrom = null;
		// Create the request params
		StringBuilder setOfFiles = new StringBuilder();		
		for (Widget w : setToCopy) {
			if (setOfFiles.length() != 0) {
				setOfFiles.append(":");
			} else {
				tempPathFrom = ((FileWidget)w).getInPath();
			}
			setOfFiles.append(((FileWidget) w).getPath());
		}
		// Necessary to get the path from we're copying. In case we want to give feedback in that folder
		final String pathFrom = tempPathFrom;
		
		// Create widget for moving or copying
		String feedbackMessage;
		if (mode.equals("move")) {
			feedbackMessage = Constants.I18N.movingNElements(setToCopy.size());
		} else {
			feedbackMessage = Constants.I18N.copyingNElements(setToCopy.size());
		}
		final Label feedbackLabel = new Label(feedbackMessage);

		Message m = new Message() {
			@Override
			public void onSuccess(Response response) {
				if (response.getStatusCode() == Response.SC_OK) {
					if (feedbackTarget.getShowingPath().equals(pathTo)) {
						// If we're pasting into the showing file: feedback
						List<FileNode> filesCopied = new ArrayList<FileNode>();
						for (Widget w : setToCopy) {
							filesCopied.add(((FileWidget)w).getNode());
						}
						feedbackTarget.addFiles(filesCopied);
						feedbackTarget.fireFilesUpdateHandlers(false, false);
					} else if (feedbackTarget.getShowingPath().equals(pathFrom)
							   && mode.equals("move")) {
						// If we're cutting from the showing file: feedback
						List<FileNode> filesCut = new ArrayList<FileNode>();
						for (Widget w : setToCopy) {
							filesCut.add(((FileWidget)w).getNode());
						}
						feedbackTarget.removeFiles(filesCut);
						feedbackTarget.fireFilesUpdateHandlers(false, false);
					}
					
					// Remove feedback
					stateProvider.removeFromTaskList(feedbackLabel);
				} else {
					// Remove feedback
					stateProvider.removeFromTaskList(feedbackLabel);
					error(mode);
				}
			}
			@Override
			public void onError() {
				// Remove feedback
				stateProvider.removeFromTaskList(feedbackLabel);
				error(mode);
			}
			@Override
			public String getURL() {
				return URL.encode(GWT.getHostPageBaseURL() + "server/" + mode);
			}
		};
		m.addParam("files", setOfFiles.toString());
		m.addParam("target", pathTo);
		SequenceHelper.sendJustOne(m, RequestBuilder.POST);
		
		// Show message in task list
		stateProvider.addToTaskList(feedbackLabel);
	}

	protected static void error(String mode) {
		if (mode.equals("move")) {
			ErrorPopup.showError(Constants.I18N.couldNotMoveFiles());
		} else {
			ErrorPopup.showError(Constants.I18N.couldNotCopyFiles());
		}
	}

	@Override
	public void executeAction() {
		doPasteAction(stateProvider.getModeCut() ? "move" : "copy",
				      stateProvider.getClipboard(),
				      stateProvider.getShowingPath(),
				      stateProvider.getModel(),
				      stateProvider);
	}

	@Override
	public Availability getAvailability() {
		if (stateProvider.getSelectedFiles().isEmpty()) {
			FolderNode n = stateProvider.getShowingFolder();
			Set<Widget> clipboard = stateProvider.getClipboard();
			if (n != null && n.isWritable()) {
				if (clipboard.isEmpty()) {
					setExtraInfo(null);
					return Availability.UNCLICKABLE;
				} else {
					// To give feedback on the number of selected files to paste
					setExtraInfo(String.valueOf(clipboard.size()));
					return Availability.AVAILABLE;
				}
			} else {
				String size = clipboard.isEmpty() ? null : String.valueOf(clipboard.size());
				setExtraInfo(size);
				return Availability.UNCLICKABLE;
			}
		} else {
			return Availability.HIDDEN;
		}
	}

}

package com.nublic.app.browser.web.client.UI.dialogs;

import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.UI.actions.NewFolderAction;

public class NewFolderContent extends FormContent {
	String inPath;
	BrowserUi feedbackTarget;

	public NewFolderContent(BrowserUi feedbackTarget) {
		super("Create new folder", Constants.DEFAULT_NEWFOLDER_TEXT);
		this.feedbackTarget = feedbackTarget;
	}

	@Override
	public void onAccept() {
		NewFolderAction.doCreateFolder(getText(), inPath, feedbackTarget);
	}

	public void setCreationPath(String createInPath) {
		inPath = createInPath;
	}
}

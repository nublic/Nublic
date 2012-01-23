package com.nublic.app.browser.web.client.UI.dialogs;

import com.nublic.app.browser.web.client.UI.actions.NewFolderAction;

public class NewFolderContent extends FormContent {
	public NewFolderContent() {
		super("Create new folder", "new folder");
	}

	@Override
	public void onAccept() {
		NewFolderAction.doCreateFolder(getText(), null);
	}
}

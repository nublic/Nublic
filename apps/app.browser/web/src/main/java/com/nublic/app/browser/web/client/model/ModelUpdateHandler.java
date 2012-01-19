package com.nublic.app.browser.web.client.model;

import com.google.gwt.event.shared.EventHandler;

public interface ModelUpdateHandler extends EventHandler {
//	void onFilesUpdate(BrowserModel m, boolean shouldUpdateFoldersOnSuccess);
	void onFoldersUpdate(BrowserModel m, FolderNode node);
	void onFilesUpdate(FileEvent e);
}

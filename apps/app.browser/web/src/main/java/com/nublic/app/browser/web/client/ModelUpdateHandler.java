package com.nublic.app.browser.web.client;

import com.google.gwt.event.shared.EventHandler;

public interface ModelUpdateHandler extends EventHandler {
	void onFilesUpdate(BrowserModel m, String path);
	void onFoldersUpdate(BrowserModel m, FolderNode node);
}

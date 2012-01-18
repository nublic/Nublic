package com.nublic.app.browser.web.client.model;

public class FileEvent {
	BrowserModel model;
	boolean shouldUpdateFoldersOnSuccess;
	boolean newFileList;
	
	public FileEvent(BrowserModel model, boolean shouldUpdateFoldersOnSuccess, boolean newFileList) {
		this.model = model;
		this.shouldUpdateFoldersOnSuccess = shouldUpdateFoldersOnSuccess;
		this.newFileList = newFileList;
	}

	public BrowserModel getModel() {
		return model;
	}

	public void setModel(BrowserModel model) {
		this.model = model;
	}

	public boolean shouldUpdateFolders() {
		return shouldUpdateFoldersOnSuccess;
	}

	public void setShouldUpdateFoldersOnSuccess(boolean shouldUpdateFoldersOnSuccess) {
		this.shouldUpdateFoldersOnSuccess = shouldUpdateFoldersOnSuccess;
	}

	public boolean isNewFileList() {
		return newFileList;
	}

	public void setNewFileList(boolean newFileList) {
		this.newFileList = newFileList;
	}
	

	
	
}

package com.nublic.app.browser.web.client.model;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.lattice.Ordering;
import com.nublic.util.lattice.PartialComparator;
import com.nublic.util.messages.Message;

public class FileMessage extends Message {
	String path;
	BrowserModel model;
	boolean shouldUpdateFoldersOnSuccess = true;
	
	public static class Comparator implements PartialComparator<FileMessage> {
		@Override
		public Ordering compare(FileMessage a, FileMessage b) {
			if (a.getSequenceNumber() > b.getSequenceNumber()) {
				return Ordering.GREATER;
			} else if (a.getSequenceNumber() == b.getSequenceNumber()) {
				return Ordering.EQUAL;
			} else {
				return Ordering.LESS;
			}
		}
	}

	public FileMessage(String path, BrowserModel model, boolean shouldUpdateFoldersOnSuccess) {
		this.path = path;
		this.model = model;
		this.shouldUpdateFoldersOnSuccess = shouldUpdateFoldersOnSuccess;
	}

	@Override
	public String getURL() {
//		String realPath = model.getDevicesManager().getRealPath(path);
//		return URL.encode(GWT.getHostPageBaseURL() + "server/files/" + realPath);
		return URL.encode(GWT.getHostPageBaseURL() + "server/files/" + path);
	}

	@Override
	public void onSuccess(Response response) {
		JsArray <FileContent> fileContentList = null;
		
		if (Response.SC_OK == response.getStatusCode()) {
			// When the call is successful
			String text = response.getText();
			fileContentList = JsonUtils.safeEval(text);
			// Update the tree with the information of folders
			if (fileContentList == null) {
				ErrorPopup.showError("Folder not found");
			} else {
				if (path == null) {
					path = new String("");
				}
				// Trim the possible firsts '/'
				while (path.length() != 0 && path.charAt(0) == '/') {
					path = path.substring(1);
				}

				model.updateFileList(fileContentList, getURL(), path);
				// Call every handler looking at the file list
				model.fireFilesUpdateHandlers(shouldUpdateFoldersOnSuccess, true);
//				for (ModelUpdateHandler handler : model.getUpdateHandlers()) {
//					handler.onFilesUpdate(model, shouldUpdateFoldersOnSuccess);
//				}
			}
		} else {
			ErrorPopup.showError("The request could not be processed");
		}
		
	}

	@Override
	public void onError() {
		ErrorPopup.showError("Server unavailable");
	}

}

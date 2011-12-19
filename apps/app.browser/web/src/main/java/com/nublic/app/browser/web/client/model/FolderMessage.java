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

public class FolderMessage extends Message {
	BrowserModel model;
	FolderNode node;
	int depth;

	public static class Comparator implements PartialComparator<FolderMessage> {
		@Override
		public Ordering compare(FolderMessage a, FolderMessage b) {
			if (a.equals(b)) {
				// Same sequence number
				return Ordering.EQUAL;
			} else if (a.getURL().equals(b.getURL())) {
				// Same path but different sequence numbers
				return Ordering.INCOMPARABLE;
			} else if (a.getURL().startsWith(b.getURL())) {
				return Ordering.LESS;
			} else if (b.getURL().startsWith(a.getURL())) {
				return Ordering.GREATER;
			} else {
				return Ordering.INCOMPARABLE;
			}
		}
	}
	
	public FolderMessage(FolderNode n, int depth, BrowserModel model) {
		node = n;
		this.depth = depth;
		this.model = model;
	}
	

	@Override
	public String getURL() {
//		String realPath = model.getDevicesManager().getRealPath(node.getPath());
//		return URL.encode(GWT.getHostPageBaseURL() + "server/folders/" + depth + "/" + realPath);
		return URL.encode(GWT.getHostPageBaseURL() + "server/folders/" + depth + "/" + node.getRealPath());
		//return URL.encode(GWT.getHostPageBaseURL() + "server/folders/" + depth + "/" + node.getPath());
	}

	@Override
	public void onSuccess(Response response) {
		JsArray <FolderContent> folderList = null;
		
		if (Response.SC_OK == response.getStatusCode()) {
			// When the call is successful
			String text = response.getText();
			folderList = JsonUtils.safeEval(text); // convert JSON to Java Object

			// Update the tree with the information of folders
			if (folderList == null) {
				ErrorPopup.showError("Folder not found");
			} else {
				model.updateTree(node, folderList);
			}

			// Call every handler looking at the folder tree
			for (ModelUpdateHandler handler : model.getUpdateHandlers()) {
				handler.onFoldersUpdate(model, node);	
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

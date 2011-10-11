package com.nublic.app.browser.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;

public class BrowserModel {
	
	Node folderTree;
	
	BrowserModel() {
		 // To initialise the tree
	    folderTree = new Node();
	}
	

	public void updateFolders(final Node n, int depth) {
		
		String pathEncoded = URL.encodePathSegment(n.getPath());
		String url = URL.encode(GWT.getHostPageBaseURL() + "server/folders/" + depth + "/" + pathEncoded);
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);

		try {
			@SuppressWarnings("unused")
			// It is not unused, we maintain callbacks
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					error("Error receiving answer from server");
				}

				public void onResponseReceived(Request request, Response response) {
					JsArray <Folder> folderList = null;
					
					if (Response.SC_OK == response.getStatusCode()) {
						// When the call is successful
						String text = response.getText();
						folderList = JsonUtils.safeEval(text);
						// Update the tree with the information of folders
						if (folderList == null) {
							error("Empty folder tree received");
						} else {
							updateTree(n, folderList);
							error("Folders updated");
						}
					} else {
						error("Bad response status");

					}
				}

			});
		} catch (RequestException e) {
			error("Exception occurred while processing update of folders");
		}
	}

	public synchronized void updateTree(Node n, JsArray<Folder> folderList) {
		updateTreeNoSync(n, folderList);
	}
	
	public void updateTreeNoSync(Node n, JsArray<Folder> folderList) {
		
		if (folderList.length() != 0) {
			// if the folder has children
			// reset the subtree - TODO: if the following line is removed, the application seems to work properly
			// except for the duplication...
			n.clear();
			// add new received data
			for (int j = 0; j < folderList.length(); j++) {
				Folder f = folderList.get(j);
				Node child = new Node(n, f);
				n.addChild(child);
				// Recursive call to update child
				updateTreeNoSync(child, f.getSubfolders());
			}
		} else {
			// reset subtree
			n.clear();
		}
	}
	
	public Node getFolderTree() {
		return folderTree;
	}
	
	public void error(String message) {
		// extender DialogBox
		Window.alert(message);
	}
	
	
	
}

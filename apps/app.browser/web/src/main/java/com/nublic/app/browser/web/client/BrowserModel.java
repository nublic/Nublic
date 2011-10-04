package com.nublic.app.browser.web.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;

public class BrowserModel {
	
	Node folderTree;
	
	BrowserModel() {
		 // To initialise the tree
	    folderTree = new Node();
	    updateFolders(folderTree, Constants.DEFAULT_DEPTH);
	}
	

	public void updateFolders(final Node n, int depth) {
		
		String pathEncoded = URL.encodePathSegment(n.getPath());
		String url = URL.encode(GWT.getModuleBaseURL() + "server/folders/" + depth + "/" + pathEncoded);
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);

		try {
			@SuppressWarnings("unused")
			// It is not unused, we maintain callbacks
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					// Do something on error
				}

				public void onResponseReceived(Request request, Response response) {
					JsArray <Folder> folderList = null;
					
					if (Response.SC_OK == response.getStatusCode()) {
						// When the call is successful
						String text = response.getText();
						folderList = JsonUtils.safeEval(text);
						// Update the tree with the information of folders
						if (folderList == null) {
							// TODO: something on error (This folder is no longer available)
						}
						updateTree(n, folderList);
					} else {
						// Do something on error
					}
				}

			});
		} catch (RequestException e) {
			// Do something on error
		}
	}

	public synchronized void updateTree(Node n, JsArray<Folder> folderList) {
		updateTreeNoSync(n, folderList);
	}
	
	public void updateTreeNoSync(Node n, JsArray<Folder> folderList) {
		
		if (folderList.length() != 0) {
			// if the folder has child
			List<Node> childrenList = new ArrayList<Node>();
			for (int j = 0; j < folderList.length(); j++) {
				Folder f = folderList.get(j);
				Node child = new Node(n, f, null);
				childrenList.add(child);
				// Recursive call to update child
				updateTreeNoSync(child, f.getSubfolders());
			}
			n.setChildren(childrenList);
		} else {
			n.setChildren(null);
		}

	}
	
	public Node getFolderTree() {
		return folderTree;
	}
	
	
	
}

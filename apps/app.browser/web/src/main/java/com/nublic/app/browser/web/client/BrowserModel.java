package com.nublic.app.browser.web.client;

import java.util.ArrayList;
import java.util.List;

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
	
	FolderNode folderTree;
	List<FileNode> fileList;
	
	ArrayList<ModelUpdateHandler> updateHandlers;

	BrowserModel() {
		 // To initialise the tree
	    folderTree = new FolderNode();
	    fileList = new ArrayList<FileNode>();
	    updateHandlers = new ArrayList<ModelUpdateHandler>();
	}

	public void addUpdateHandler(ModelUpdateHandler handler) {	 	
	    updateHandlers.add(handler);
	}
	
	// Getters
	public FolderNode getFolderTree() {
		return folderTree;
	}

	public List<FileNode> getFileList() {
		return fileList;
	}
	
	// Server request methods
	public void updateFolders(final FolderNode n, int depth) {
		
		// TODO: Sequence numbers to "ignore" unupdated responses
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
					JsArray <FolderContent> folderList = null;
					
					if (Response.SC_OK == response.getStatusCode()) {
						// When the call is successful
						String text = response.getText();
						folderList = JsonUtils.safeEval(text);
						// Update the tree with the information of folders
						if (folderList == null) {
							error("Empty folder tree received");
						} else {
							//FolderContent.sortList(folderList);
							updateTree(n, folderList);
						}

						// Call every handler looking at the folder tree
						for (ModelUpdateHandler handler : updateHandlers) {	 	
							handler.onFoldersUpdate(BrowserModel.this, n);	
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
	
	public void updateFiles(final ParamsHashMap params) {
		String path = params.get(Constants.BROWSER_PATH_PARAMETER);
		if (path == null) {
			path = new String("");
		}
		String pathEncoded = URL.encodePathSegment(path);
		String url = URL.encode(GWT.getHostPageBaseURL() + "server/files/" + pathEncoded);
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);

		try {
			@SuppressWarnings("unused")
			// It is not unused, we maintain callbacks
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					error("Error receiving answer from server");
				}

				public void onResponseReceived(Request request, Response response) {
					JsArray <FileContent> fileContentList = null;
					
					if (Response.SC_OK == response.getStatusCode()) {
						// When the call is successful
						String text = response.getText();
						fileContentList = JsonUtils.safeEval(text);
						// Update the tree with the information of folders
						if (fileContentList == null) {
							error("Wrong file list received");
						} else {
							updateFileList(fileContentList);
						}
						
						// Call every handler looking at the file list
						for (ModelUpdateHandler handler : updateHandlers) {
							String path = params.get(Constants.BROWSER_PATH_PARAMETER);
							if (path == null) {
								path = new String("");
							}
							handler.onFilesUpdate(BrowserModel.this, path);
							//handler.onFilesUpdate(BrowserModel.this, params.get(Constants.BROWSER_PATH_PARAMETER));
						}
					} else {
						error("Bad response status");
					}
				}

			});
		} catch (RequestException e) {
			error("Exception occurred while processing update of files");
		}
	}

	// Update methods for responses
	public synchronized void updateTree(FolderNode n, JsArray<FolderContent> folderList) {
		updateTreeNoSync(n, folderList);
	}
	
	public void updateTreeNoSync(FolderNode n, JsArray<FolderContent> folderList) {
		if (folderList.length() != 0) {
			// if the folder has children
			n.clear();
			// add new received data
			for (int i = 0; i < folderList.length(); i++) {
				FolderContent f = folderList.get(i);
				FolderNode child = new FolderNode(n, f.getName());
				n.addChild(child);
				// Recursive call to update child
				updateTreeNoSync(child, f.getSubfolders());
			}
		} else {
			// reset subtree
			n.clear();
		}
	}
	
	public synchronized void updateFileList(JsArray<FileContent> fileContentList) {
		if (fileContentList.length() != 0) {
			fileList.clear();
			for (int i = 0; i < fileContentList.length(); i++) {
				FileContent fileContent = fileContentList.get(i);
				FileNode file = new FileNode(fileContent);
				fileList.add(file);
			}
		} else {
			// The requested folder is empty
			fileList.clear();
		}
	}
	
	// Other methods
	public synchronized FolderNode createBranch(String path) {
		if (path.equals("")) {
			return folderTree;
		}

		String splited[] = path.split("/");

		FolderNode currentNode = folderTree;
		for (int i = 0; i < splited.length ; i++) {
			FolderNode newNode = currentNode.getChild(splited[i]);
			if (newNode == null) {
				newNode = new FolderNode(currentNode, splited[i]);
				currentNode.addChild(newNode);
			}
			currentNode = newNode;
		}

		return currentNode;
	}
	
	public void error(String message) {
		// TODO: extend DialogBox
		Window.alert(message);
	}


}

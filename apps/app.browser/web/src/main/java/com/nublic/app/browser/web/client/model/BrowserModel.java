package com.nublic.app.browser.web.client.model;

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
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.error.ErrorPopup;
import com.nublic.util.messages.SequenceIgnorer;
import com.nublic.util.messages.SequenceWaiter;

public class BrowserModel {
	SequenceWaiter<FolderMessage> foldersMessageHelper;
	SequenceIgnorer<FileMessage> filesMessageHelper;
	FolderNode folderTree;
	List<FileNode> fileList;
	String showingURL;

	ArrayList<ModelUpdateHandler> updateHandlers;

	public BrowserModel() {
		 // To initialise the tree
	    folderTree = new FolderNode();
	    fileList = new ArrayList<FileNode>();
	    updateHandlers = new ArrayList<ModelUpdateHandler>();
	    showingURL = "";
	    foldersMessageHelper = new SequenceWaiter<FolderMessage>(new FolderMessage.Comparator());
	    filesMessageHelper = new SequenceIgnorer<FileMessage>(new FileMessage.Comparator());
	}

	public void addUpdateHandler(ModelUpdateHandler handler) {	 	
	    updateHandlers.add(handler);
	}
	
	public List<ModelUpdateHandler> getUpdateHandlers() {
		return updateHandlers;
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
		
		FolderMessage message = new FolderMessage(n, depth, this);
		foldersMessageHelper.send(message, RequestBuilder.GET);
		
//		String url = URL.encode(GWT.getHostPageBaseURL() + "server/folders/" + depth + "/" + n.getPath());
//		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);

//		try {
//			@SuppressWarnings("unused")
//			// It is not unused, we maintain callbacks
//			Request request = builder.sendRequest(null, new RequestCallback() {
//				public void onError(Request request, Throwable exception) {
//					ErrorPopup.showError("Server unavailable");
//				}
//
//				public void onResponseReceived(Request request, Response response) {
//					JsArray <FolderContent> folderList = null;
//					
//					if (Response.SC_OK == response.getStatusCode()) {
//						// When the call is successful
//						String text = response.getText();
//						folderList = JsonUtils.safeEval(text);
//						// Update the tree with the information of folders
//						if (folderList == null) {
//							ErrorPopup.showError("Folder not found");
//						} else {
//							//FolderContent.sortList(folderList);
//							updateTree(n, folderList);
//						}
//
//						// Call every handler looking at the folder tree
//						for (ModelUpdateHandler handler : updateHandlers) {	 	
//							handler.onFoldersUpdate(BrowserModel.this, n);	
//						}
//						
//					} else {
//						ErrorPopup.showError("The request could not be processed");
//					}
//				}
//
//			});
//		} catch (RequestException e) {
//			ErrorPopup.showError("Fatal error");
//		}
	}
	
	public void updateFiles(final ParamsHashMap params) {
		String path = params.get(Constants.PATH_PARAMETER);
		if (path == null) {
			path = new String("");
		}
		FileMessage message = new FileMessage(path, this);
		
		if (!showingURL.equals(message.getURL())) {
			filesMessageHelper.send(message, RequestBuilder.GET);
		}
		
//		final String url = URL.encode(GWT.getHostPageBaseURL() + "server/files/" + path);
//		if (!showingURL.equals(url)) {
//			// If the browser is already showing the requested URL we won't update anything
//			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
//
//			try {
//				@SuppressWarnings("unused")
//				// It is not unused, we maintain callbacks
//				Request request = builder.sendRequest(null, new RequestCallback() {
//					public void onError(Request request, Throwable exception) {
//						ErrorPopup.showError("Server unavailable");
//					}
//	
//					public void onResponseReceived(Request request, Response response) {
//						JsArray <FileContent> fileContentList = null;
//						
//						if (Response.SC_OK == response.getStatusCode()) {
//							// When the call is successful
//							String text = response.getText();
//							fileContentList = JsonUtils.safeEval(text);
//							// Update the tree with the information of folders
//							if (fileContentList == null) {
//								ErrorPopup.showError("Folder not found");
//							} else {
//								updateFileList(fileContentList, url);
//	
//								// Call every handler looking at the file list
//								for (ModelUpdateHandler handler : updateHandlers) {
//									String path = params.get(Constants.PATH_PARAMETER);
//									if (path == null) {
//										path = new String("");
//									}
//									// Trim the possible firsts '/'
//									while (path.length() != 0 && path.charAt(0) == '/') {
//										path = path.substring(1);
//									}
//									handler.onFilesUpdate(BrowserModel.this, path);
//								}
//							}
//						} else {
//							ErrorPopup.showError("The request could not be processed");
//						}
//					}
//
//				});
//			} catch (RequestException e) {
//				ErrorPopup.showError("Fatal error");
//			}
//		}
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
	
	public synchronized void updateFileList(JsArray<FileContent> fileContentList, String url) {
		showingURL = url;
		if (fileContentList.length() != 0) {
			fileList.clear();
			for (int i = 0; i < fileContentList.length(); i++) {
				FileContent fileContent = fileContentList.get(i);
				FileNode file = new FileNode(fileContent.getName(), fileContent.getMime(), fileContent.getView());
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
	
}

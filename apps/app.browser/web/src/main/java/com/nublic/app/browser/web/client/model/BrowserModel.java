package com.nublic.app.browser.web.client.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.RequestBuilder;
import com.nublic.app.browser.web.client.devices.DeviceMessage;
import com.nublic.app.browser.web.client.devices.DevicesManager;
import com.nublic.util.messages.SequenceHelper;
import com.nublic.util.messages.SequenceIgnorer;
import com.nublic.util.messages.SequenceWaiter;

public class BrowserModel {
	SequenceWaiter<FolderMessage> foldersMessageHelper;
	SequenceIgnorer<FileMessage> filesMessageHelper;
	FolderNode folderTree;
	// "State" dependent part
	List<FileNode> fileList;
	String showingURL;
	String showingPath;
	DevicesManager devManager;
	
	// TODO: synchronize updateFileList over an object to allow synchronized reading

	ArrayList<ModelUpdateHandler> updateHandlers;

	public BrowserModel() {
		 // To initialise the tree
	    folderTree = new FolderNode();
	    fileList = new ArrayList<FileNode>();
	    updateHandlers = new ArrayList<ModelUpdateHandler>();
	    showingURL = "";
	    showingPath = "";
	    foldersMessageHelper = new SequenceWaiter<FolderMessage>(new FolderMessage.Comparator());
	    filesMessageHelper = new SequenceIgnorer<FileMessage>(new FileMessage.Comparator());
	    devManager = new DevicesManager();
	}

	public void addUpdateHandler(ModelUpdateHandler handler) {	 	
	    updateHandlers.add(handler);
	}
	
	public List<ModelUpdateHandler> getUpdateHandlers() {
		return updateHandlers;
	}
	
	public void fireUpdateHandlers(FolderNode node) {
		// Call every handler looking at the folder tree
		for (ModelUpdateHandler handler : updateHandlers) {
			handler.onFoldersUpdate(this, node);	
		}
	}
	
	// Getters
	public FolderNode getFolderTree() {
		return folderTree;
	}

	public List<FileNode> getFileList() {
		return fileList;
	}
	
	public String getShowingPath() {
		return showingPath;
	}

	public FolderNode getShowingFolder() {
		// Add the showing path to the tree in case we've overwritten it
		// (we've cleaned everything because we want to allow deletion updates in server to be shown,
		// but if we're showing a path we need to have the nodes in the tree...)
		return createBranch(showingPath);
//		List<String> pathTokens = devManager.splitPath(showingPath);
//		
//		if (pathTokens.get(0).equals("")) {
//			return folderTree;
//		} else {
//			FolderNode returnFolder = folderTree.getChild(pathTokens.get(0));
//			int i = 1;
//			while (i < pathTokens.size() && returnFolder != null) {
//				returnFolder = returnFolder.getChild(pathTokens.get(i));
//				i++;
//			}
//			return returnFolder;
//		}
	}
	
	public DevicesManager getDevicesManager() {
		return devManager;
	}
	
	public SequenceWaiter<FolderMessage> getFoldersMessageHelper(){
		return foldersMessageHelper;
	}
	
	// Server request methods
	public void updateFolders(final FolderNode n, int depth) {
		if (n.equals(folderTree)) {
			DeviceMessage m = new DeviceMessage(devManager, this);
			SequenceHelper.sendJustOne(m, RequestBuilder.GET);
		} else {
			FolderMessage message = new FolderMessage(n, depth, this);
			foldersMessageHelper.send(message, RequestBuilder.GET);
		}
	}
	
	public void updateFiles(String path, boolean shouldUpdateFoldersOnSuccess) {
		FileMessage message = new FileMessage(path, this, shouldUpdateFoldersOnSuccess);
		if (!showingURL.equals(message.getURL())) {
			filesMessageHelper.send(message, RequestBuilder.GET);
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
				FolderNode child = new FolderNode(n, f.getName(), f.getWritable());
				n.addChild(child);
				// Recursive call to update child
				updateTreeNoSync(child, f.getSubfolders());
			}
		} else {
			// reset subtree
			n.clear();
		}
	}
	
	public synchronized void updateFileList(JsArray<FileContent> fileContentList, String url, String path) {
		showingURL = url;
		showingPath = path;
		if (fileContentList.length() != 0) {
			fileList.clear();
			for (int i = 0; i < fileContentList.length(); i++) {
				FileContent fileContent = fileContentList.get(i);
				FileNode file = new FileNode(fileContent.getName(),
											 fileContent.getMime(),
											 fileContent.getView(),
											 fileContent.getSize(),
											 fileContent.getLastUpdate(),
											 fileContent.getWritable());
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

		List<String> splited = devManager.splitPath(path);

		FolderNode currentNode = folderTree;
		for (String s : splited) {
			FolderNode newNode = currentNode.getChild(s);
			// If the node is still not created in the tree, we create it
			if (newNode == null) {
				// We define created nodes as writable (we don't have the information and the server will not let write it them if they are not)
				// If the user enters the folder the information will be reloaded properly, it's made this way only to enable drag and drop in all the cases
				newNode = new FolderNode(currentNode, s, true);
				currentNode.addChild(newNode);
			}
			currentNode = newNode;
		}

		return currentNode;
	}
	
	// It returns null if the node representing the required path doesn't exists
	public synchronized FolderNode search(String path) {
		if (path.equals("")) {
			return folderTree;
		}

		List<String> splited = devManager.splitPath(path);

		FolderNode currentNode = folderTree;
		for (String s : splited) {
			FolderNode newNode = currentNode.getChild(s);
			// If the node is created we go through it, else return null
			if (newNode != null) {
				currentNode = newNode;
			} else {
				return null;
			}
		}

		return currentNode;
	}
	
}

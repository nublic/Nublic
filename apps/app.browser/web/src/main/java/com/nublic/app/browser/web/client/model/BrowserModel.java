package com.nublic.app.browser.web.client.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.Timer;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.devices.DeviceMessage;
import com.nublic.app.browser.web.client.devices.DevicesManager;
import com.nublic.util.messages.SequenceHelper;
import com.nublic.util.messages.SequenceIgnorer;
import com.nublic.util.messages.SequenceWaiter;

public class BrowserModel {
	SequenceWaiter<FolderMessage> foldersMessageHelper;
	SequenceIgnorer<FileMessage> filesMessageHelper;
	SequenceWaiter<ChangesMessage> changesMessageHelper;
	FolderNode folderTree;
	// "State" dependent part
	List<FileNode> fileList;
	List<FileNode> modifiedFiles;
	String showingPath;
	DevicesManager devManager;
	double lastUpdateTime;
	Timer filesPollingTimer = new Timer() {
		@Override
		public void run() {
			reUpdateFiles();
		}
	};
	
	// TODO: synchronize updateFileList over an object to allow synchronized reading

	ArrayList<ModelUpdateHandler> updateHandlers;

	public BrowserModel() {
		 // To initialise the tree
	    folderTree = new FolderNode();
	    fileList = new ArrayList<FileNode>();
	    modifiedFiles = new ArrayList<FileNode>();
	    updateHandlers = new ArrayList<ModelUpdateHandler>();
	    showingPath = "";
	    foldersMessageHelper = new SequenceWaiter<FolderMessage>(new FolderMessage.Comparator());
	    filesMessageHelper = new SequenceIgnorer<FileMessage>(new FileMessage.Comparator());
	    changesMessageHelper = new SequenceWaiter<ChangesMessage>(new ChangesMessage.Comparator());
	    devManager = new DevicesManager();
	}

	public void addUpdateHandler(ModelUpdateHandler handler) {	 	
	    updateHandlers.add(handler);
	}
	
	public List<ModelUpdateHandler> getUpdateHandlers() {
		return updateHandlers;
	}
	
	public void fireFolderUpdateHandlers(FolderNode node) {
		// Call every handler looking at the folder tree
		for (ModelUpdateHandler handler : updateHandlers) {
			handler.onFoldersUpdate(this, node);	
		}
	}

	public void fireFilesUpdateHandlers(boolean shouldUpdateFoldersOnSuccess, boolean newFileList) {
		fireFilesUpdateHandlers(new FileEvent(this, shouldUpdateFoldersOnSuccess, newFileList));
	}
	
	public void fireFilesUpdateHandlers(FileEvent e) {
		// Call every handler looking at the files
		for (ModelUpdateHandler handler : updateHandlers) {
			handler.onFilesUpdate(e);	
		}
	}
	
	// Getters
	public FolderNode getFolderTree() {
		return folderTree;
	}

	public List<FileNode> getFileList() {
		return fileList;
	}
	
	public List<FileNode> getModifiedFiles() {
		return modifiedFiles;
	}
	
	public void clearModifiedFiles() {
		modifiedFiles.clear();
	}
	
	public String getShowingPath() {
		return showingPath;
	}

	public FolderNode getShowingFolder() {
		// Add the showing path to the tree in case we've overwritten it
		// (we've cleaned everything because we want to allow deletion updates in server to be shown,
		// but if we're showing a path we need to have the nodes in the tree...)
		return createBranch(showingPath);
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
		if (path.equals("")) {
			// If root path, files gets updated with devices
			changePath("");
			fireFilesUpdateHandlers(false, true);
		} else {
			if (!showingPath.equals(path)) {
				FileMessage message = new FileMessage(path, this, shouldUpdateFoldersOnSuccess);
				filesMessageHelper.send(message, RequestBuilder.GET);
			}
		}
	}

	public void reUpdateFiles() {
		ChangesMessage cm = new ChangesMessage(this, showingPath, lastUpdateTime);
		lastUpdateTime = cm.getCreationDate();
		changesMessageHelper.send(cm, RequestBuilder.GET);
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
		changePath(path);
		if (fileContentList.length() != 0) {
			fileList.clear();
			for (int i = 0; i < fileContentList.length(); i++) {
				FileContent fileContent = fileContentList.get(i);
				addFile(fileContent.getName(), fileContent.getMime(),	    fileContent.getView(),
						fileContent.getSize(), fileContent.getLastUpdate(), fileContent.getWritable(),
						fileContent.hasThumbnail());
			}
		} else {
			// The requested folder is empty
			fileList.clear();
		}
	}
	
	public synchronized void addFile(String name, String mime, String view, double size, double lastUpdate, boolean writable, boolean has_thumbnail) {
		FileNode file = new FileNode(name, mime, view, size, lastUpdate, writable, has_thumbnail);
		fileList.add(file);
	}
	
	public synchronized void addFiles(List<FileNode> filesToAdd) {
		// TODO: Check if it is more efficient to maintain a "Set<FileNode> fileList" all the time. (I think we should migrate to this when we've got time)
		Set<FileNode> setToAdd = Sets.newHashSet(filesToAdd);
		Set<FileNode> modified = Sets.intersection(Sets.newHashSet(fileList), setToAdd);
		modifiedFiles.addAll(modified);
		fileList.addAll(Sets.difference(setToAdd, modified));
	}
	
	public synchronized void removeFiles(List<FileNode> filesToRemove) {
		fileList.removeAll(filesToRemove);
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
	
	private void changePath(String newPath) {
		showingPath = newPath;
		modifiedFiles.clear();
		resetTimer();
	}
	
	public void resetTimer() {
		lastUpdateTime = new Date().getTime();
		scheduleNewTimer();
	}
	
	public void scheduleNewTimer() {
		filesPollingTimer.cancel();
		if (!showingPath.equals("")) {
			// It doesn't make sense to ask for changes in root folders as it works in a different way
			filesPollingTimer.schedule(Constants.TIME_TO_POLLING);
		}
	}
}

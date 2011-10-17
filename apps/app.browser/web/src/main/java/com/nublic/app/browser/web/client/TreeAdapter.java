package com.nublic.app.browser.web.client;

import java.util.Stack;

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class TreeAdapter {
	BrowserModel model;
	Tree treeView;

	public TreeAdapter(Tree treeView, BrowserModel model) {
		this.treeView = treeView;
		this.model = model;
	}

	public synchronized void updateView(FolderNode node) {
		// Searches and/or creates the node on the tree
		TreeItem nodeView = search(node);
		
		if (nodeView == null) {
			// We're updating the root of the tree
			// (the FolderNode (node) passed was the root node)
			treeView.removeItems();
			for (FolderNode child : node.getChildren()) {
				updateRootView(child);
			}
		} else {
			nodeView.removeItems();
			for (FolderNode child : node.getChildren()) {
				updateNodeView(nodeView, child);
			}
		}

	}

	private void updateRootView(FolderNode node) {
		TreeItem newNode = treeView.addItem(node.getName());
		newNode.setUserObject(node);

		for (FolderNode child : node.getChildren()) {
			updateNodeView(newNode, child);
		}
	}

	private void updateNodeView(TreeItem nodeView, FolderNode node) {
		TreeItem newNode = nodeView.addItem(node.getName());
		newNode.setUserObject(node);

		for (FolderNode child : node.getChildren()) {
			updateNodeView(newNode, child);
		}
	}

	// Searches for the node on the viewTree and if it doesn't exists this method creates the path to it.
	// If we want to maintain the model abstract from the view it's necessary to go through the treeView each time.
	private TreeItem search(FolderNode node) {
		Stack<FolderNode> pathStack = node.getPathStack();
		FolderNode firstInStack = null;
		TreeItem nodeView = null;
		TreeItem childNode = null;
		boolean found = false;
		
		if (pathStack.isEmpty()) {
			return null;
		}
		
		firstInStack = pathStack.pop();

		// This first step cannot be done with the rest because the first level is of a different type
		// Iterates through nodes in treeView until it finds the desired node  
		for (int i = 0 ; i < treeView.getItemCount() && !found; i++){
			nodeView = treeView.getItem(i);
			found = nodeView.getHTML().equals(firstInStack.getName());
		}
		
		// If it hasn't been found we have to create the complete path stack in the tree view 
		if  (!found) {
			nodeView = treeView.addItem(firstInStack.getName());
			nodeView.setUserObject(firstInStack);
			return createNewBranch(nodeView, pathStack);
		}
		
		// The same as before for the TreeItem type of the rest of the tree
		// Iteration in java style (FolderNode node : pathStack) doesn't pop things out 
		FolderNode nodeInStack = null;
		while (!pathStack.isEmpty()) {
			nodeInStack = pathStack.pop();
			found = false;
			for (int i = 0 ; i < nodeView.getChildCount() && !found ; i++) {
				childNode = nodeView.getChild(i);
				found = childNode.getHTML().equals(nodeInStack.getName());
			}
			if  (!found) {
				childNode = nodeView.addItem(nodeInStack.getName());
				childNode.setUserObject(nodeInStack);
				return createNewBranch(childNode, pathStack);
			}
			nodeView = childNode;
		}

		// If we reach this point we will have found all the path in the viewTree
		return nodeView;
	}

	// Creates a new branch for the given pathStack of names from the given point (nodeView)
	private TreeItem createNewBranch(TreeItem nodeView, Stack<FolderNode> pathStack) {
		TreeItem createdNode = nodeView;

		FolderNode node = null;
		while (!pathStack.isEmpty()) {
			node = pathStack.pop();
			createdNode = createdNode.addItem(node.getName());
			createdNode.setUserObject(node);
		}

		return createdNode;
	}

}

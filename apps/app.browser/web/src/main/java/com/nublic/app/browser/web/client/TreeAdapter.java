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

	public void updateView(FolderNode node) {
		// Searches and/or creates the node on the tree
		TreeItem nodeView = search(node);
		
		nodeView.removeItems();
		for (FolderNode child : node.getChildren()) {
			updateNodeView(nodeView, child);
		}

	}

	private void updateNodeView(TreeItem nodeView, FolderNode node) {
		TreeItem newNode = nodeView.addItem(node.getContent().getName());

		for (FolderNode child : node.getChildren()) {
			updateNodeView(newNode, child);
		}
	}

	// Searches for the node on the viewTree and if it doesn't exists this method creates the path to it.
	// If we want to maintain the model abstract from the view it's necessary to go through the treeView each time.
	private TreeItem search(FolderNode node) {
		Stack<String> pathStack = node.getPathStack();
		String nodeName1 = pathStack.pop();
		TreeItem nodeView = null;
		boolean found = false;

		// This first step cannot be done with the rest because the first level is of a different type
		// Iterates through nodes in treeView until it finds the desired node  
		for (int i = 0 ; i < treeView.getItemCount() && !found; i++){
			nodeView = treeView.getItem(i);
			found = nodeView.getHTML().equals(nodeName1);
		}
		
		// If it hasn't been found we have to create the complete path stack in the tree view 
		if  (!found) {
			nodeView = treeView.addItem(nodeName1);
			return createNewBranch(nodeView, pathStack);
		}
		
		// The same as before for the TreeItem type of the rest of the tree
		for (String nodeName2 : pathStack) {
			found = false;
			for (int i = 0 ; i < treeView.getItemCount() && !found ; i++) {
				nodeView = treeView.getItem(i);
				found = nodeView.getHTML().equals(nodeName2);
			}
			if  (!found) {
				nodeView = nodeView.addItem(nodeName2);
				return createNewBranch(nodeView, pathStack);
			}
		}

		// If we reach this point we will have found all the path in the viewTree
		return nodeView;
	}

	// Creates a new branch for the given pathStack of names from the given point (nodeView)
	private TreeItem createNewBranch(TreeItem nodeView, Stack<String> pathStack) {
		TreeItem createdNode = nodeView;

		for (String nodeName : pathStack) {
			createdNode = createdNode.addItem(nodeName);
		}

		return createdNode;
	}

}

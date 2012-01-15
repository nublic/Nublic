package com.nublic.app.browser.web.client.UI;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.nublic.app.browser.web.client.model.BrowserModel;
import com.nublic.app.browser.web.client.model.FolderNode;

public class TreeAdapter {
	BrowserModel model;
	Tree treeView;
//	TreeItem mouseOver;
	
	public TreeAdapter(Tree treeView, BrowserModel model) {
		this.treeView = treeView;
		this.model = model;
	}

//	public TreeItem getMouseOver() {
//		
//		return mouseOver;
//	}
//	
//	public void setMouseOver(TreeItem t) {
//		mouseOver = t; 
//	}
	
//	private void addOverHandler(final TreeItem newNode) {
//		if (newNode != null) {
//			newNode.sinkEvents(Event.ONMOUSEOVER);
//			DOM.setEventListener(newNode.getElement(), new EventListener() {
//				@Override
//				public void onBrowserEvent(Event event) {
//					switch (event.getTypeInt()) {
//					case Event.ONMOUSEOVER:
//						mouseOver = newNode;
//						break;
//					}
//				}
//			});
//			DOM.sinkEvents(newNode.getElement(), Event.ONMOUSEOVER);
////			Widget w = newNode.getWidget();
////			if (w != null) {
////				w.addDomHandler(new MouseOverHandler() {
////					@Override
////					public void onMouseOver(MouseOverEvent event) {
////						mouseOver = newNode;
////					}
////				}, MouseOverEvent.getType());
////			}
//		}
//	}
	
	public Iterator<TreeItem> getVisibleIterator() {
		return getVisibleList().iterator();
	}
	
	public ArrayList<TreeItem> getVisibleList() {
		ArrayList<TreeItem> iterableList = new ArrayList<TreeItem>();
		for (int i = 0; i < treeView.getItemCount(); i++) {
			TreeItem node = treeView.getItem(i);
			iterableList.add(node);
			if (node.getState()) {
				addVisibleChildren(iterableList, node);
			}
		}
		return iterableList;
	}

	private void addVisibleChildren(List<TreeItem> iterableList, TreeItem node) {
		for (int i = 0; i < node.getChildCount(); i++) {
			TreeItem child = node.getChild(i);
			iterableList.add(child);
			if (child.getState()) {
				addVisibleChildren(iterableList, child);
			}
		}
	}

	private TreeItem createNewNode(TreeItem from, FolderNode node) {
		TreeItem newNode = from.addItem(node.getName());
		newNode.setUserObject(node);
//		addOverHandler(newNode);
		return newNode;
	}
	
	private TreeItem createNewNode(Tree from, FolderNode node) {
		TreeItem newNode = from.addItem(node.getName());
		newNode.setUserObject(node);
//		addOverHandler(newNode);
		return newNode;
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
		
		// In case we have overwrite the selected node // TODO: check if this is necessary
		FolderNode showingFolder = model.getShowingFolder();
		if (showingFolder.isDescendantOf(node)) {
			treeView.setSelectedItem(search(showingFolder));
		}
	}

	private void updateRootView(FolderNode node) {
		TreeItem newNode = createNewNode(treeView, node);

		for (FolderNode child : node.getChildren()) {
			updateNodeView(newNode, child);
		}
	}

	private void updateNodeView(TreeItem nodeView, FolderNode node) {
		TreeItem newNode = createNewNode(nodeView, node);

		for (FolderNode child : node.getChildren()) {
			updateNodeView(newNode, child);
		}
	}

	// Searches for the node on the viewTree and if it doesn't exists this method creates the path to it.
	// If we want to maintain the model abstract from the view it's necessary to go through the treeView each time.
	public synchronized TreeItem search(FolderNode node) {
		Stack<FolderNode> pathStack = node.getPathStack();
		FolderNode firstInStack = null;
		TreeItem nodeView = null;
		TreeItem childNode = null;
		boolean found = false;
		
		if (pathStack.isEmpty()) {
			return null;
		}
		
		firstInStack = pathStack.pop();

		// This first step cannot be done with the rest because the first level has a different type
		// Iterates through nodes in treeView until it finds the desired node  
		for (int i = 0 ; i < treeView.getItemCount() && !found; i++){
			nodeView = treeView.getItem(i);
			found = firstInStack.equals(nodeView.getUserObject());
//			found = ((FolderNode)nodeView.getUserObject()).getRealPath().equals(firstInStack.getRealPath());
		}
		
		// If it hasn't been found we have to create the complete path stack in the tree view 
		if  (!found) {
			nodeView = createNewNode(treeView, firstInStack);
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
				found = nodeInStack.equals(childNode.getUserObject());
				//found = ((FolderNode)childNode.getUserObject()).getRealPath().equals(nodeInStack.getRealPath());
			}
			if  (!found) {
				childNode = createNewNode(nodeView, nodeInStack);
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
			createdNode = createNewNode(createdNode, node);
		}

		return createdNode;
	}

}

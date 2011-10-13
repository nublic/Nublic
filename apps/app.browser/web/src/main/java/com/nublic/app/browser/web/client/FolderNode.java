package com.nublic.app.browser.web.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.view.client.ListDataProvider;

public class FolderNode {
	FolderContent content;
	FolderNode parent;
	List<FolderNode> children;
	ListDataProvider<FolderNode> dataProvider;
	
	// Constructors
	FolderNode() {
		parent = null;
		content = null;
		children = new ArrayList<FolderNode>();
		//dataProvider = new ListDataProvider<Node>();
		dataProvider = null;
	}
	
	FolderNode(FolderNode parent, FolderContent content) {
		this.parent = parent;
		this.content = content;
		children = new ArrayList<FolderNode>();
		dataProvider = new ListDataProvider<FolderNode>(children);
		
//		dataProvider = new ListDataProvider<Node>();
//		children = dataProvider.getList();
		
//		children = new ArrayList<Node>();
//		dataProvider = new ListDataProvider<Node>();
//		dataProvider.setList(children);
	}
	


	// Getters and Setters
	public FolderContent getContent() {
		return content;
	}
	
	public void setContent(FolderContent content) {
		this.content = content;
	}
	
	public FolderNode getParent() {
		return parent;
	}
	
	public void setParent(FolderNode parent) {
		this.parent = parent;
	}
	
	public List<FolderNode> getChildren() {
		return children;
	}
	
	public void addChild(FolderNode child) {
		children.add(child);
	}
	
	public void replaceChild(int j, FolderNode child) {
		if (j >= children.size()) {
			children.add(child);
		} else {
			ListDataProvider<FolderNode> tempProvider = children.get(j).getDataProvider();
			child.setDataProvider(tempProvider);
			children.set(j, child);
		}
	}
	
	public void clear() {
		children.clear();
	}

	
	public ListDataProvider<FolderNode> getDataProvider() {
		return dataProvider;
	}
	
	public void setDataProvider(ListDataProvider<FolderNode> dataProv) {
		dataProvider = dataProv;
		//dataProvider.setList(children);
		children = dataProvider.getList();
		for (int i = 0; i < children.size(); i++) {
			// for some strange reason children.clear() doesn't work
			children.remove(i);
		}
	}

	// Calculates and returns the path to this node
	public String getPath() {
		if (parent == null) {
			return "";
		} else {
			return parent.getPath(content.getName());
		}
	}
	
	// Calculates and returns the path to this node
	public String getPath(String accumulated) {
		if (parent == null) {
			return accumulated;
		} else {
			return parent.getPath(content.getName() + "/" + accumulated);
		}
	}
	
}

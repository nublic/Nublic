package com.nublic.app.browser.web.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.view.client.ListDataProvider;

public class Node {
	NodeContent content;
	Node parent;
	List<Node> children;
	ListDataProvider<Node> dataProvider;
	
	// Constructors
	Node() {
		parent = null;
		content = null;
		children = new ArrayList<Node>();
		//dataProvider = new ListDataProvider<Node>();
		dataProvider = null;
	}
	
	Node(Node parent, NodeContent content) {
		this.parent = parent;
		this.content = content;
		children = new ArrayList<Node>();
		dataProvider = new ListDataProvider<Node>(children);
		
//		dataProvider = new ListDataProvider<Node>();
//		children = dataProvider.getList();
		
//		children = new ArrayList<Node>();
//		dataProvider = new ListDataProvider<Node>();
//		dataProvider.setList(children);
	}
	


	// Getters and Setters
	public NodeContent getContent() {
		return content;
	}
	
	public void setContent(NodeContent content) {
		this.content = content;
	}
	
	public Node getParent() {
		return parent;
	}
	
	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	public List<Node> getChildren() {
		return children;
	}
	
	public void addChild(Node child) {
		children.add(child);
	}
	
	public void replaceChild(int j, Node child) {
		if (j >= children.size()) {
			children.add(child);
		} else {
			ListDataProvider<Node> tempProvider = children.get(j).getDataProvider();
			child.setDataProvider(tempProvider);
			children.set(j, child);
		}
	}
	
	public void clear() {
		children.clear();
	}

	
	public ListDataProvider<Node> getDataProvider() {
		return dataProvider;
	}
	
	public void setDataProvider(ListDataProvider<Node> dataProv) {
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

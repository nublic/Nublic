package com.nublic.app.browser.web.client;

import java.util.List;

public class Node {
	NodeContent content;
	Node parent;
	List<Node> children;
	
	// Constructors
	Node() {
		parent = null;
		content = null;
		children = null;
	}
	
	Node(Node parent, NodeContent content, List<Node> children) {
		this.parent = parent;
		this.content = content;
		this.children = children;
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
	
	public void setChildren(List<Node> children) {
		this.children = children;
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

package com.nublic.app.browser.web.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class Node extends JavaScriptObject {
	NodeContent content;
	Node father;
	JsArray <Node> child;
	
	// Constructors
	Node() {
		father = null;
		content = null;
		child = null;
	}
	
	Node(Node father, NodeContent content, JsArray<Node> child) {
		this.father = father;
		this.content = content;
		this.child = child;
	}
	
	// Getters and Setters
	public NodeContent getContent() {
		return content;
	}
	
	public void setContent(NodeContent content) {
		this.content = content;
	}
	
	public Node getFather() {
		return father;
	}
	
	public void setFather(Node father) {
		this.father = father;
	}
	
	public JsArray<Node> getChild() {
		return child;
	}
	
	public void setChild(JsArray<Node> child) {
		this.child = child;
	}

	// Calculates and returns the path to this node
	public String getPath() {
		if (father == null) {
			return "/";
		} else {
			return father.getPath(content.getName());
		}
	}
	
	// Calculates and returns the path to this node
	public String getPath(String accumulated) {
		if (father == null) {
			return "/" + accumulated;
		} else {
			return father.getPath(content.getName() + "/" + accumulated);
		}
	}
}

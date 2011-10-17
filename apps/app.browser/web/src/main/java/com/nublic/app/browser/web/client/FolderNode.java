package com.nublic.app.browser.web.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FolderNode {
	FolderContent content;
	FolderNode parent;
	List<FolderNode> children;
	
	// Constructors
	FolderNode() {
		parent = null;
		content = null;
		children = new ArrayList<FolderNode>();
	}
	
	FolderNode(FolderNode parent, FolderContent content) {
		this.parent = parent;
		this.content = content;
		children = new ArrayList<FolderNode>();
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
	
	public void clear() {
		children.clear();
	}

	// Calculates and returns the path to this node
	public String getPath() {
		if (parent == null) {
			return "";
		} else {
			return parent.getPath(content.getName());
		}
	}

	public String getPath(String accumulated) {
		if (parent == null) {
			return accumulated;
		} else {
			return parent.getPath(content.getName() + "/" + accumulated);
		}
	}
	
	// same as getPath, but returns the result in a ArrayList <FolderNode> instead in URL format
	// TODO: can be done with a for instead recursion
	public Stack<FolderNode> getPathStack() {
		Stack<FolderNode> stack = new Stack<FolderNode>();
		if (parent == null) {
			return stack;
		} else {
			stack.push(this);
			return parent.getPathStack(stack);
		}
	}
	
	public Stack<FolderNode> getPathStack(Stack<FolderNode> accumStack) {
		if (parent == null) {
			return accumStack;
		} else {
			accumStack.push(this);
			return parent.getPathStack(accumStack);
		}
	}
	
}

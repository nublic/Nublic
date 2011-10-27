package com.nublic.app.browser.web.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FolderNode {
	String name;
	FolderNode parent;
	List<FolderNode> children;
	
	
	// Constructors
	FolderNode() {
		parent = null;
		name = null;
		children = new ArrayList<FolderNode>();
	}
	
	FolderNode(FolderNode parent, String name) {
		this.parent = parent;
		this.name = name;
		children = new ArrayList<FolderNode>();
	}

	// Getters and Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
			return parent.getPath(name);
		}
	}

	public String getPath(String accumulated) {
		if (parent == null) {
			return accumulated;
		} else {
			return parent.getPath(name + "/" + accumulated);
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

	public FolderNode getChild(String name) {
		for (FolderNode child : children){
			if (child.getName().equals(name)) {
				return child;
			}
		}
		
		return null;
	}
	
}

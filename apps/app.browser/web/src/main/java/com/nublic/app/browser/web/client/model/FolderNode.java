package com.nublic.app.browser.web.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FolderNode {
	String name;
	String pathName;
	FolderNode parent;
	List<FolderNode> children;
	boolean writable;

	// Constructors
	public FolderNode() {
		parent = null;
		name = null;
		pathName = null;
		writable = false;
		children = new ArrayList<FolderNode>();
	}
	
	public FolderNode(FolderNode parent, String name, boolean writable) {
		this(parent, name, name, writable);
	}
	
	public FolderNode(FolderNode parent, String name, String pathName, boolean writable) {
		this.parent = parent;
		this.name = name;
		this.pathName = pathName;
		this.writable = writable;
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
	
	public void removeChild(FolderNode child) {
		children.remove(child);
	}
	
	public void clear() {
		children.clear();
	}

	public boolean isWritable() {
		return writable;
	}
	
	public boolean isDescendantOf(FolderNode n) {
		String myRealPath = getRealPath();
		String nRealPath = n.getRealPath();
		
		return myRealPath.startsWith(nRealPath);
	}

	public boolean isParentOf(FolderNode n) {
		return n.isDescendantOf(this);
	}
	
	public String getPathName() {
		return pathName;
	}

	// Calculates and returns the real path to this node
	public String getRealPath() {
		if (parent == null) {
			return "";
		} else {
			return parent.getRealPath(pathName);
		}
	}

	public String getRealPath(String accumulated) {
		if (parent == null) {
			return accumulated;
		} else {
			return parent.getRealPath(pathName + "/" + accumulated);
		}
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
	
	// Same as getPath, but returns the result in a Stack <FolderNode> instead in URL format
	public Stack<FolderNode> getPathStack() {
		Stack<FolderNode> stack = new Stack<FolderNode>();
		FolderNode current = this;
		while (current.getParent() != null) {
			stack.push(current);
			current = current.getParent();
		}
		return stack;
	}

	// DONE (upside): can be done with a for instead recursion
//	public Stack<FolderNode> getPathStack() {
//		Stack<FolderNode> stack = new Stack<FolderNode>();
//		if (parent == null) {
//			return stack;
//		} else {
//			stack.push(this);
//			return parent.getPathStack(stack);
//		}
//	}
//	
//	public Stack<FolderNode> getPathStack(Stack<FolderNode> accumStack) {
//		if (parent == null) {
//			return accumStack;
//		} else {
//			accumStack.push(this);
//			return parent.getPathStack(accumStack);
//		}
//	}

	public FolderNode getChild(String name) {
		for (FolderNode child : children) {
			if (child.getPathName().equals(name)) {
				return child;
			}
		}
		return null;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof FolderNode) {
			if (((FolderNode) o).getRealPath().equals(getRealPath())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
}

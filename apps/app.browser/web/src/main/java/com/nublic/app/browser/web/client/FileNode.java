package com.nublic.app.browser.web.client;


public class FileNode {
	FileContent content;

	// Constructors
	FileNode() {
		content = null;
	}
	
	FileNode(FileContent content) {
		this.content = content;
	}

	// Getters and Setters
	public FileContent getContent() {
		return content;
	}
	
	public void setContent(FileContent content) {
		this.content = content;
	}

	
}

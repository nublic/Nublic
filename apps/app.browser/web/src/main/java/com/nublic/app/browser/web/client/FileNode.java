package com.nublic.app.browser.web.client;


public class FileNode {
	String name;
	String mime;
	String view;

	// Constructors
	public FileNode() {
		name = null;
		mime = null;
		view = null;
	}

	public FileNode(String name, String mime, String view) {
		this.name = name;
		this.mime = mime;
		this.view = view;
	}

	// Getters and Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getMime() {
		return mime;
	}
	
	public void setMime(String mime) {
		this.mime = mime;
	}
	
	public String getView() {
		return view;
	}
	
	public void setView(String view) {
		this.view = view;
	}
	
}

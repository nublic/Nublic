package com.nublic.app.browser.web.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class Folder extends JavaScriptObject implements NodeContent {
	String name;
	JsArray <Folder> subfolders;

	protected Folder () { }
	
	
	public final native String getName();
//	{
//		return name;
//	}


	// TODO: es necesario crear setters?
	public void setName(String name) {
		this.name = name;
	}


	public final native JsArray<Folder> getSubfolders();
//	{
//		return subfolders;
//	}


	public void setSubfolders(JsArray<Folder> subfolders) {
		this.subfolders = subfolders;
	}

}

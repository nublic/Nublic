package com.nublic.app.browser.web.client.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class FolderContent extends JavaScriptObject {

	protected FolderContent () { }
	
	public final native String getName() /*-{
		return this.name;
    }-*/;

	public final native JsArray<FolderContent> getSubfolders() /*-{
	 	return this.subfolders;
    }-*/;

	public final native boolean getWritable() /*-{
		return this.writable;
	}-*/;

}

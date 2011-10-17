package com.nublic.app.browser.web.client;

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

}

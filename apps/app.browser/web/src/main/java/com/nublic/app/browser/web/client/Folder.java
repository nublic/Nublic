package com.nublic.app.browser.web.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class Folder extends JavaScriptObject implements NodeContent {

	protected Folder () { }
	
	public final native String getName() /*-{
	  return this.name;
    }-*/;

	public final native JsArray<Folder> getSubfolders() /*-{
	  return this.subfolders;
    }-*/;

}

package com.nublic.app.browser.web.client.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;

public class ChangesContent extends JavaScriptObject {

	protected ChangesContent () { }
	
	public final native JsArray<FileContent> getNewFiles() /*-{
	 	return this.new_files;
    }-*/;

	public final native JsArrayString getDeletedFiles() /*-{
 		return this.deleted_files;
	}-*/;

}
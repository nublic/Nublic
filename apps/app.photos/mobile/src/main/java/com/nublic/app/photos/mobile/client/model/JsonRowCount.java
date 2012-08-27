package com.nublic.app.photos.mobile.client.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class JsonRowCount extends JavaScriptObject {

	protected JsonRowCount () { }
	
	public final native int getRowCount() /*-{
	  return this.row_count;
    }-*/;

	public final native JsArray<JsonPhoto> getPhotos() /*-{
	  return this.photos;
    }-*/;
}

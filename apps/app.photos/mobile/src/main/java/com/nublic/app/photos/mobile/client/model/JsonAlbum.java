package com.nublic.app.photos.mobile.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class JsonAlbum extends JavaScriptObject {

	protected JsonAlbum () { }
	
	public final native int getId() /*-{
	  return this.id;
    }-*/;

	public final native String getName() /*-{
	  return this.name;
    }-*/;
}

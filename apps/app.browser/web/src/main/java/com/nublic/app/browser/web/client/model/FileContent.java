package com.nublic.app.browser.web.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class FileContent extends JavaScriptObject {

	protected FileContent () { }
	
	public final native String getName() /*-{
	  return this.name;
    }-*/;

	public final native String getMime() /*-{
	  return this.mime;
    }-*/;
	
	public final native String getView() /*-{
	  return this.view;
  	}-*/;
	
	private final native int _getSize() /*-{
	  return this.size;
	}-*/;
	
	public final long getSize() {
		return (long)_getSize();
	}
	
	private final native double _getLastUpdate() /*-{
	  return this.last_update;
	}-*/;
	
	public final long getLastUpdate() {
		return (long)_getLastUpdate();
	}

}

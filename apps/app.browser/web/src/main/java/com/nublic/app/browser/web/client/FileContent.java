package com.nublic.app.browser.web.client;

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

}

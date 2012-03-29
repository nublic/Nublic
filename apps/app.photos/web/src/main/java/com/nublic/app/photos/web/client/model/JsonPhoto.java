package com.nublic.app.photos.web.client.model;

import java.util.Date;

import com.google.gwt.core.client.JavaScriptObject;

public class JsonPhoto extends JavaScriptObject {

	protected JsonPhoto () { }
	
	public final native int getId() /*-{
	  return this.id;
    }-*/;

	public final native String getTitle() /*-{
	  return this.title;
    }-*/;
	
	public Date getDate() {
		return new Date(Math.round(_getDate()));
	}
	
	public final native double _getDate() /*-{
	  return this.date;
  }-*/;
}

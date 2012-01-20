package com.nublic.app.manager.settings.client.comm;

import com.google.gwt.core.client.JavaScriptObject;

public class NublicUser extends JavaScriptObject {

	protected NublicUser () { }
	
	public final native String getShownName() /*-{
		return this.name;
    }-*/;

	public final native String getUserName() /*-{
		return this.username;
	}-*/;

}

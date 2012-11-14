package com.nublic.app.init.client.model.js;

import com.google.gwt.core.client.JavaScriptObject;

// user ::= { "username" : $username,
//			  "shownname": $shownname }
public class JSUser extends JavaScriptObject {

	protected JSUser() { }

	public final native String getUserName() /*-{
		return this.username;
    }-*/;
	
	public final native String getShownName() /*-{
		return this.shownname;
	}-*/;
}

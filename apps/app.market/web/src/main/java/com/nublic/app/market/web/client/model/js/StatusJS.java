package com.nublic.app.market.web.client.model.js;

import com.google.gwt.core.client.JavaScriptObject;

public class StatusJS extends JavaScriptObject {
	protected StatusJS() { }
	
	public final native String getStatus() /*-{
		return this.status;
	}-*/;
}

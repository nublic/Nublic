package com.nublic.app.manager.settings.client.comm;

import com.google.gwt.core.client.JavaScriptObject;

public class NublicSyncedFolder extends JavaScriptObject {

	protected NublicSyncedFolder () { }
	
	public final native String getName() /*-{
		return this.name;
    }-*/;

	public final native int getId() /*-{
		return this.id;
	}-*/;

}

package com.nublic.app.browser.web.client.devices;

import com.google.gwt.core.client.JavaScriptObject;

public class JSDevice extends JavaScriptObject {

	protected JSDevice() { }
	
	public final native int getId() /*-{
		return this.id;
	}-*/;
	
	public DeviceKind getKind() {
		return DeviceKind.parse(_getKind());
	}
	
	private final native String _getKind() /*-{
		return this.kind;
	}-*/;
	
	public final native String getName() /*-{
		return this.name;
    }-*/;

	public final native boolean getOwner() /*-{
		return this.owner;
	}-*/;

}

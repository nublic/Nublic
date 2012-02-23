package com.nublic.app.music.client.datamodel.js;

import com.google.gwt.core.client.JavaScriptObject;


//playlist ::= { "id"   : $id
//			   , "name" : $name }
public class JSPlaylist extends JavaScriptObject {

	protected JSPlaylist() { }

	final public String getId() {
		return String.valueOf(_getId());
	}

	public final native int _getId() /*-{
		return this.id;
	}-*/;
	
	public final native String getName() /*-{
		return this.name;
    }-*/;

}

package com.nublic.app.music.client.datamodel.js;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;


// return ::= { "row_count": $row-count, "songs": [ song, song, ... ] }
public class JSSongResponse extends JavaScriptObject {

	protected JSSongResponse() { }

	public final native int getRowCount() /*-{
		return this.row_count;
	}-*/;
	
	public final native JsArray<JSSong> getSongs() /*-{
		return this.songs;
    }-*/;

}

package com.nublic.app.music.client.datamodel.js;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;


// return ::= { "row_count": $row-count, "albums": [ album, album, ... ] }
public class JSAlbumResponse extends JavaScriptObject {

	protected JSAlbumResponse() { }

	public final native int getRowCount() /*-{
		return this.row_count;
	}-*/;
	
	public final native JsArray<JSAlbum> getAlbums() /*-{
		return this.albums;
    }-*/;

}

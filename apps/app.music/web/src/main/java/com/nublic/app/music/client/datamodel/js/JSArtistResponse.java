package com.nublic.app.music.client.datamodel.js;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;


// return ::= { "row_count": $row-count, "artists": [ artist, artist, ... ] }
public class JSArtistResponse extends JavaScriptObject {

	protected JSArtistResponse() { }

	public final native int getRowCount() /*-{
		return this.row_count;
	}-*/;
	
	public final native JsArray<JSArtist> getArtists() /*-{
		return this.artists;
    }-*/;

}

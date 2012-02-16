package com.nublic.app.music.client.datamodel.js;

import com.google.gwt.core.client.JavaScriptObject;

// album ::= { "id" : $album-id,
//			   "name": $name,
//			   "songs": $number_of_songs,
//			   $extra_info }
public class JSAlbum extends JavaScriptObject {

	protected JSAlbum() { }

	final public String getId() {
		return String.valueOf(_getId());
	}

	public final native int _getId() /*-{
		return this.id;
	}-*/;
	
	public final native String getName() /*-{
		return this.name;
    }-*/;

	public final native int getSongs() /*-{
		return this.songs;
	}-*/;
}

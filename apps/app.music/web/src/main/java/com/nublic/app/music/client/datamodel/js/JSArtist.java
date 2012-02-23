package com.nublic.app.music.client.datamodel.js;

import com.google.gwt.core.client.JavaScriptObject;

//artist ::= { "id" : $artist-id,
//           "name": $name,
//           "discs": $number_of_discs,
//           "songs": $number_of_songs,
//           $extra_info }
public class JSArtist extends JavaScriptObject {

	protected JSArtist() { }

	final public String getId() {
		return String.valueOf(_getId());
	}

	public final native int _getId() /*-{
		return this.id;
	}-*/;
	
	public final native String getName() /*-{
		return this.name;
    }-*/;

	public final native int getAlbums() /*-{
		return this.albums;
	}-*/;
	
	public final native int getSongs() /*-{
		return this.songs;
	}-*/;
}

package com.nublic.app.music.client.datamodel.js;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayInteger;

// album ::= { "id" : $album-id,
//			   "name": $name,
//			   "songs": $number_of_songs,
//             "artists": [ $artist-id, $artist-id, ... ]
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
	
	public final native JsArrayInteger _getArtists() /*-{
		return this.artists;
	}-*/;
	
	public final List<String> getArtists() {
		List<String> returnList = new ArrayList<String>();
		JsArrayInteger jsList = _getArtists();
		for (int i = 0; i < jsList.length(); i++) {
			returnList.add(String.valueOf(jsList.get(i)));
		}
		return returnList;
	}
}

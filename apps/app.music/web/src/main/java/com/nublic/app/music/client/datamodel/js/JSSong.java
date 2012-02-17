package com.nublic.app.music.client.datamodel.js;

import com.google.gwt.core.client.JavaScriptObject;

//song   ::= { "id": $song-id,
//           "title": $title,
//           "artist_id": $artist-id,
//           "album_id": $album-id,
//           $extra_info }
public class JSSong extends JavaScriptObject {

	protected JSSong() { }

	final public String getId() {
		return String.valueOf(_getId());
	}

	public final native int _getId() /*-{
		return this.id;
	}-*/;
	
	public final native String getTitle() /*-{
		return this.title;
    }-*/;
	
	final public String getArtistId() {
		return String.valueOf(_getArtistId());
	}

	public final native int _getArtistId() /*-{
		return this.artist_id;
	}-*/;

	final public String getAlbumId() {
		return String.valueOf(_getAlbumId());
	}

	public final native int _getAlbumId() /*-{
		return this.album_id;
	}-*/;
	
	public final native int getTrack() /*-{
		if (this.track === undefined || this.track === null) {
			return -1;
		} else {
			return this.track;
		}
	}-*/;
	
	public final native int getLength() /*-{
		return this.length;
	}-*/;
}

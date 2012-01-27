package com.nublic.app.music.client.datamodel.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.datamodel.Artist;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.util.messages.Message;

//GET /albums/:artist-id/:asc-desc/:start/:length/:colid/:colid/...
//* Retrieve all albums for the artist which live in any of the tags
//* Possible values for asc-decs: "asc" or "desc"
//* To retrieve all albums not depending on artist-id, use "all" as :artist-id
//* If no tags are given, it means "in any tag"
//* Return: return ::= { "row_count": $row-count, "albums": [ album, album, ... ] }
//          album  ::= { "id" : $album-id,
//                       "name": $name,
//                       "songs": $number_of_songs,
//                       $extra_info }

public class AlbumMessage extends Message {
	Artist artist = null;
	DataModel model = null;
	String artistId = null;
	
	public AlbumMessage(DataModel model, String artistId) {
		this.model = model;
		this.artistId = artistId;
	}

	public AlbumMessage(Artist a) {
		artist = a;
	}

	@Override
	public String getURL() {
		StringBuilder url = new StringBuilder();
		url.append(GWT.getHostPageBaseURL());
		url.append("server/albums/");
		if (artist == null) {
			url.append(artistId);			
		} else {
			url.append(artist.getId());
		}
		url.append("/desc/0/32000");
		return URL.encode(url.toString());
	}

	@Override
	public void onSuccess(Response response) {
		if (artist == null) {
			
		} else {
			
			artist.fireAlbumsHandler();
		}
	}

	@Override
	public void onError() {
		onSuccess(null);
	}

}

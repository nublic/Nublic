package com.nublic.app.music.client.datamodel.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.datamodel.Album;
import com.nublic.app.music.client.datamodel.Artist;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.State;
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
	// One of this groups..
	// Artist
	Artist artist = null;
	// Or model, id and collection (where artist and collection can be null)
	DataModel model = null;
	String artistId = null;
	String inCollection = null;
	
	public AlbumMessage(DataModel model, String artistId, String inCollection) {
		this.model = model;
		this.artistId = artistId;
		this.inCollection = inCollection;
	}
	
	public AlbumMessage(DataModel model, String artistId) {
		this(model, artistId, null);
	}
	
	public AlbumMessage(DataModel model) {
		this(model, null, null);
	}

	public AlbumMessage(Artist a) {
		artist = a;
	}

	@Override
	public String getURL() {
		StringBuilder url = new StringBuilder();
		url.append(GWT.getHostPageBaseURL());
		url.append("server/albums/");
		// Add possible artist filter
		if (artist != null) {
			url.append(artist.getId());
		} else if (artistId != null) {
			url.append(artistId);
		} else {
			url.append("all");
		}
		url.append("/desc/0/32000");
		// Add possible collection filter
		if (artist != null && artist.getInCollection() != null) {
			url.append("/");
			url.append(artist.getInCollection());
		} else if (inCollection != null) {
			url.append("/");
			url.append(inCollection);
		}
		
		return URL.encode(url.toString());
	}

	@Override
	public void onSuccess(Response response) {
		if (artist == null) {
			// For album messages directly for data model
			model.clearAlbumList();
			// TODO: Fake info to try..
				model.addAlbum(new Album("AlbumId1", "Vinagre y Rosas", 10));
				model.addAlbum(new Album("AlbumId2", "Origins of symmetry", 10));
				model.addAlbum(new Album("AlbumId3", "Bad", 10));
				model.addAlbum(new Album("AlbumId4", "Be here now", 10));
			// Fake info end
			model.setState(State.ALBUM_SONGS);
			model.fireStateHandlers();
			// TODO: ask for songs if proceeds (if not using async data provider lists)
		} else {
			// For album messages filling some artist
			artist.clearAlbumList();
			// TODO: Fake info to try
				artist.addAlbum(new Album("AlbumId1", "Vinagre y Rosas", 10, artist.getInCollection(), artist));
				artist.addAlbum(new Album("AlbumId2", "Origins of symmetry", 10, artist.getInCollection(), artist));
				artist.addAlbum(new Album("AlbumId3", "Bad", 10, artist.getInCollection(), artist));
				artist.addAlbum(new Album("AlbumId4", "Be here now", 10, artist.getInCollection(), artist));
			// Fake info end
			artist.fireAlbumsHandler();
		}
	}

	@Override
	public void onError() {
		onSuccess(null);
	}

}

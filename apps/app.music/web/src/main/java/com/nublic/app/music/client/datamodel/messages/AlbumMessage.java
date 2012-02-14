package com.nublic.app.music.client.datamodel.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.datamodel.Album;
import com.nublic.app.music.client.datamodel.AlbumInfo;
import com.nublic.app.music.client.datamodel.Artist;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.State;
import com.nublic.app.music.client.datamodel.js.JSAlbum;
import com.nublic.util.cache.Cache;
import com.nublic.util.error.ErrorPopup;
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
	Cache<String, AlbumInfo> albumCache = null;
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

	public AlbumMessage(Artist a, Cache<String, AlbumInfo> albumCache) {
		artist = a;
		this.albumCache = albumCache;
	}

	@Override
	public String getURL() {
		StringBuilder url = new StringBuilder();
		url.append(GWT.getHostPageBaseURL());
		url.append("server/albums/");
		// Add possible artist filter
		if (artist != null) {
			url.append(artist.getInfo().getId());
		} else if (artistId != null) {
			url.append(artistId);
		} else {
			url.append("all");
		}
		url.append("/desc/0/32000/");
		// Add possible collection filter
		if (artist != null && artist.getInCollection() != null) {
			url.append(artist.getInCollection());
		} else if (inCollection != null) {
			url.append(inCollection);
		}
		
		return URL.encode(url.toString());
	}

	@Override
	public void onSuccess(Response response) {
		if (response.getStatusCode() == Response.SC_OK) {
			// Commented for response including row count.
//			JSAlbumResponse jsResponse = null;
			String text = response.getText();
//			jsResponse = JsonUtils.safeEval(text);
			JsArray<JSAlbum> jsResponse = JsonUtils.safeEval(text);
			if (artist == null) {
				// For album messages directly for data model
				if (jsResponse == null) {
					onError();
				} else {
					insertResponseInModel(jsResponse);
				}
			} else {
				// For album messages filling some artist
				if (jsResponse == null) {
					onError();
				} else {
					insertResponseInArtist(jsResponse);
				}
			}
		} else {
			onError();
		}
	}

	private void insertResponseInArtist(JsArray<JSAlbum> jsResponse) {
		artist.clearAlbumList();
//		JsArray<JSAlbum> albumList = jsResponse.getAlbums();
//		for (int i = 0; i < albumList.length(); i++) {
//			JSArtist album = albumList.get(i);
		for (int i = 0; i < jsResponse.length(); i++) {
			JSAlbum album = jsResponse.get(i);

			AlbumInfo info = new AlbumInfo(album.getId(), album.getName(), album.getSongs());
			artist.addAlbum(new Album(info));
			albumCache.put(album.getId(), info);
		}
		artist.fireAlbumsHandler();
		
	}

	private void insertResponseInModel(JsArray<JSAlbum> jsResponse) {
		model.clearAlbumList();
//		JsArray<JSAlbum> albumList = jsResponse.getAlbums();
//		for (int i = 0; i < albumList.length(); i++) {
//			JSArtist album = albumList.get(i);
		for (int i = 0; i < jsResponse.length(); i++) {
			JSAlbum album = jsResponse.get(i);

			AlbumInfo info = new AlbumInfo(album.getId(), album.getName(), album.getSongs());
			model.addAlbum(new Album(info));
			model.getAlbumCache().put(album.getId(), info);
		}
		model.setState(State.ALBUM_SONGS);
		model.setShowingArtistId(artistId);
		model.fireStateHandlers();	
		// TODO: ask for songs if proceeds (if not using async data provider lists)
	}

	@Override
	public void onError() {
		ErrorPopup.showError("Could not get albums");
	}

}

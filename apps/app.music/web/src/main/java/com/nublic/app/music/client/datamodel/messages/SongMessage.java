package com.nublic.app.music.client.datamodel.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.datamodel.Album;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.Song;
import com.nublic.app.music.client.datamodel.js.JSSong;
import com.nublic.app.music.client.datamodel.js.JSSongResponse;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.Message;

//GET /songs/:artist-id/:album-id/:order/:asc-desc/:start/:length/:colid/:colid/...
//* Retrieve the songs in range [start, start + length - 1] by an artist in an album
//  (use "all" in any of those to tell it is unspecified) with an specified order
//* Possible orders: alpha -> alphabetical by song title
//                   artist_alpha -> first artist name, then song title
//                   artist_album -> first artist name, then album name, then disc_no, then track
//                   album -> first album name, then disc_no, then track
//* Possible values for asc-decs: "asc" or "desc"
//* Return: return ::= { "row_count": $row-count, "songs": [ song, song, ... ] }
//          song   ::= { "id": $song-id,
//                       "title": $title,
//                       "artist-id": $artist-id,
//                       "album-id": $album-id,
//                       $extra_info }

public class SongMessage extends Message {
	// One of this groups..
	// Album
	Album album = null;
	// Or model, id and collection (where artist, album and collection can be null)
	DataModel model = null;
	String artistId = null;
	String albumId = null;
	String inCollection = null;
	int from = 0;
	int to = 25;
	
	public SongMessage(DataModel model, String artistId, String albumId, String inCollection) {
		this.model = model;
		this.artistId = artistId;
		this.albumId = albumId;
		this.inCollection = inCollection;
	}
	
	public SongMessage(DataModel model, String artistId, String albumId) {
		this(model, artistId, albumId, null);
	}
	
	public SongMessage(DataModel model, String artistId) {
		this(model, artistId, null, null);
	}
	
	public SongMessage(DataModel model) {
		this(model, null, null);
	}

	public SongMessage(Album a) {
		this(0, 32000, a);
	}
	
	public SongMessage(int from, int to, Album a) {
		this.from = from;
		this.to = to;
		album = a;
	}

	@Override
	public String getURL() {
		StringBuilder url = new StringBuilder();
		url.append(GWT.getHostPageBaseURL());
		url.append("server/songs/");
		// Add possible filters
		if (album != null) {
			// Artist filter
			if (album.getInArtist() != null) {
				url.append(album.getInArtist().getInfo().getId());
				url.append("/");
			} else {
				url.append("all/");
			}
			// Album filter
			url.append(album.getInfo().getId());
		} else {
			// Artist filter
			if (artistId != null) {
				url.append(artistId);
				url.append("/");
			} else {
				url.append("all/");
			}
			// Album filter
			if (albumId != null) {
				url.append(albumId);
			} else {
				url.append("all");
			}
		}
		// Range of request
		url.append("/");
		url.append(Constants.ORDER_ALBUM);
		url.append("/asc/");
		url.append(from);
		url.append("/");
		url.append(to - from + 1);
		url.append("/");
		// Add possible collection filter
		if (album != null && album.getInCollection() != null) {
			url.append(album.getInCollection());
		} else if (inCollection != null) {
			url.append(inCollection);
		}
		
		return URL.encode(url.toString());
	}

	@Override
	public void onSuccess(Response response) {
		if (response.getStatusCode() == Response.SC_OK) {
			// Commented for response including row count.
			JSSongResponse jsResponse = null;
			String text = response.getText();
			jsResponse = JsonUtils.safeEval(text);
			if (album == null) {
				// For song messages directly for data model
				if (jsResponse == null) {
					onError();
				} else {
					insertResponseInModel(jsResponse);
				}
			} else {
	//			// For song messages filling some album
				if (jsResponse == null) {
					onError();
				} else {
					insertResponseInAlbum(jsResponse);
				}
			}
		} else {
			onError();
		}
	}

	private void insertResponseInAlbum(JSSongResponse jsResponse) {
//		album.clearAlbumList();
		JsArray<JSSong> songList = jsResponse.getSongs();
		for (int i = 0; i < songList.length(); i++) {
			JSSong song = songList.get(i);
			Song info = new Song(song.getId(),
								 song.getTitle(),
								 song.getArtistId(),
								 song.getAlbumId(),
								 song.getTrack(),
								 song.getLength());
			album.addSong(from + i, info);
		}
		album.fireSongHandlers(from, to);
	}

	private void insertResponseInModel(JSSongResponse jsResponse) {
//		model.clearSongList();
		JsArray<JSSong> songList = jsResponse.getSongs();
		for (int i = 0; i < songList.length(); i++) {
			JSSong song = songList.get(i);

			//AlbumInfo info = new AlbumInfo(album.getId(), album.getName(), album.getSongs());
			model.addSong(from + i, new Song(song.getId(),
											 song.getTitle(),
											 song.getArtistId(),
											 song.getAlbumId(),
											 song.getTrack(),
											 song.getLength()));
			//model.getAlbumCache().put(album.getId(), info);
		}
		model.fireSongHandlers(from, to, jsResponse.getRowCount());
	}

	@Override
	public void onError() {
		ErrorPopup.showError("Could not get songs");
	}

}

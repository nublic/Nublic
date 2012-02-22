package com.nublic.app.music.client.datamodel.messages;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.app.music.client.datamodel.handlers.SongHandler;
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
	
	String artistId = null;
	String albumId = null;
	String inCollection = null;
	int from = 0;
	int to = 25;

	// Handler handling
	SongHandler songHandler;
	// Necessary to know if handler must be called
	int targetScreen;
	DataModel model;
	
	public SongMessage(String album, String collection, SongHandler sh, int currentScreen, DataModel model) {
		this.albumId = album;
		this.inCollection = collection;
		this.songHandler = sh;
		this.targetScreen = currentScreen;
		this.model = model;
	}
	
//	public SongMessage(int from, int to, AlbumInfo a) {
//		this.from = from;
//		this.to = to;
//		album = a;
//	}

	@Override
	public String getURL() {
		StringBuilder url = new StringBuilder();
		url.append(GWT.getHostPageBaseURL());
		url.append("server/songs/");
		// Add possible filters
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

		// Range of request
		url.append("/");
		url.append(Constants.ORDER_ALBUM);
		url.append("/asc/");
		url.append(from);
		url.append("/");
		url.append(to - from + 1);
		url.append("/");
		// Add possible collection filter
		if (inCollection != null) {
			url.append(inCollection);
		}
		
		return URL.encode(url.toString());
	}

	@Override
	public void onSuccess(Response response) {
		if (response.getStatusCode() == Response.SC_OK) {
			// Commented for response including row count.
			String text = response.getText();
			JSSongResponse jsResponse = JsonUtils.safeEval(text);			
			if (jsResponse == null) {
				onError();
			} else {
				List<SongInfo> answerList = new ArrayList<SongInfo>();
				JsArray<JSSong> songList = jsResponse.getSongs();
				for (int i = 0; i < songList.length(); i++) {
					JSSong song = songList.get(i);
					SongInfo info = new SongInfo(song.getId(),
										 song.getTitle(),
										 song.getArtistId(),
										 song.getAlbumId(),
										 song.getTrack(),
										 song.getLength());
					answerList.add(info);
				}
				// Only if the message arrives on time to fill the screen it was meant for
				if (targetScreen == model.getCurrentScreen()) {
					songHandler.onSongsChange(from, to, answerList);
				}
			}
		} else {
			onError();
		}
	}

	@Override
	public void onError() {
		ErrorPopup.showError("Could not get songs");
	}

}

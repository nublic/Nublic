package com.nublic.app.music.client.datamodel.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.datamodel.Artist;
import com.nublic.app.music.client.datamodel.ArtistInfo;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.State;
import com.nublic.app.music.client.datamodel.js.JSArtist;
import com.nublic.app.music.client.datamodel.js.JSArtistResponse;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.Message;

//GET /artists/:asc-desc/:start/:length/:colid/:colid/...
//* Retrieve all artists which live in any of the tags
//* Possible values for asc-decs: "asc" or "desc"
//* If no collection ids are given, it means "all artists"
//* Return: return ::= { "row_count": $row-count, "artists": [ artist, artist, ... ] }
//          artist ::= { "id" : $artist-id,
//                       "name": $name,
//                       "discs": $number_of_discs,
//                       "songs": $number_of_songs,
//                       $extra_info }
public class ArtistMessage extends Message {
	String collectionId;
	DataModel model;
	
	public ArtistMessage(DataModel model) {
		this(model, null);
	}

	public ArtistMessage(DataModel model, String collection) {
		this.model = model;
		collectionId = collection;
	}

	@Override
	public String getURL() {
		StringBuilder url = new StringBuilder();
		url.append(GWT.getHostPageBaseURL());
		url.append("server/artists");
		url.append("/desc/0/32000/");
		if (collectionId != null) {
			url.append(collectionId);
		}
		return URL.encode(url.toString());
	}

	@Override
	public void onSuccess(Response response) {
		// Fill artists list in model with the new info
		model.clearArtistList();

		// Commented for response including row count.
		JSArtistResponse jsResponse = null;
		if (response.getStatusCode() == Response.SC_OK) {
			String text = response.getText();
			jsResponse = JsonUtils.safeEval(text);

			if (jsResponse == null) {
				onError();
			} else {
				JsArray<JSArtist> artistList = jsResponse.getArtists();
				for (int i = 0; i < artistList.length(); i++) {
					JSArtist artist = artistList.get(i);
					ArtistInfo info = new ArtistInfo(artist.getId(), artist.getName(), artist.getDiscs(), artist.getSongs());
					model.addArtist(new Artist(info, collectionId));
					model.getArtistCache().put(artist.getId(), info);
				}
			}
		} else {
			onError();
		}
			
		// Set the selected collection which is being shown
		if (collectionId == null) {
			// All music
			model.setShowing();
		} else {
			model.setShowing(collectionId, true);
		}
		model.setState(State.ARTIST_ALBUMS);		
		model.fireStateHandlers();		
	}

	@Override
	public void onError() {
		ErrorPopup.showError("Could not get artist from server");
	}

	public static ArtistInfo parseArtistInfo(Response r) {
		String text = r.getText();
		JSArtist artist = JsonUtils.safeEval(text);
		
		return new ArtistInfo(artist.getId(), artist.getName(), artist.getDiscs(), artist.getSongs());
	}

}

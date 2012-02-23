package com.nublic.app.music.client.datamodel.messages;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.datamodel.ArtistInfo;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.handlers.ArtistHandler;
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
	
	// Handler handling
	ArtistHandler artistHandler;
	// Necessary to know if handler must be called
	int targetScreen;
	DataModel model;
	

	public ArtistMessage(String collection, ArtistHandler ah, int currentScreen, DataModel model) {
		collectionId = collection;
		artistHandler = ah;
		this.targetScreen = currentScreen;
		this.model = model;
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
		
		if (response.getStatusCode() == Response.SC_OK) {
			JSArtistResponse jsResponse = null;
			String text = response.getText();
			jsResponse = JsonUtils.safeEval(text);

			if (jsResponse == null) {
				onError();
			} else {
				List<ArtistInfo> answerList = new ArrayList<ArtistInfo>(); // To be filled and returned
				JsArray<JSArtist> artistList = jsResponse.getArtists();
				for (int i = 0; i < artistList.length(); i++) {
					JSArtist artist = artistList.get(i);
					ArtistInfo info = new ArtistInfo(artist.getId(), artist.getName(), artist.getAlbums(), artist.getSongs());
					answerList.add(info);
					// We take the opportunity to add the artist to the model cache
					model.getArtistCache().put(info.getId(), info);
				}
				
				// Only if the message arrives on time to fill the screen it was meant for
				if (targetScreen == model.getCurrentScreen()) {
					artistHandler.onArtistChange(answerList);
				}
			}
		} else {
			onError();
		}
	}

	@Override
	public void onError() {
		ErrorPopup.showError("Could not get artist from server");
	}

	public static ArtistInfo parseArtistInfo(Response r) {
		String text = r.getText();
		JSArtist artist = JsonUtils.safeEval(text);
		
		return new ArtistInfo(artist.getId(), artist.getName(), artist.getAlbums(), artist.getSongs());
	}

}

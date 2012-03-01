package com.nublic.app.music.client.datamodel.messages;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.datamodel.AlbumInfo;
import com.nublic.app.music.client.datamodel.Controller;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.handlers.AlbumHandler;
import com.nublic.app.music.client.datamodel.js.JSAlbum;
import com.nublic.app.music.client.datamodel.js.JSAlbumResponse;
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
	String artistId;
	String inCollection;
	
	// Handler handling
	AlbumHandler albumHandler;
	// Necessary to know if handler must be called
	int targetScreen;
	DataModel model;

	public AlbumMessage(String artistId, String inCollection, AlbumHandler ah, int currentScreen, DataModel model) {
		this.artistId = artistId;
		this.inCollection = inCollection;
		this.albumHandler = ah;
		this.targetScreen = currentScreen;
		this.model = model;
	}

	@Override
	public String getURL() {
		StringBuilder url = new StringBuilder();
		url.append(GWT.getHostPageBaseURL());
		url.append("server/albums/");
		// Add possible artist filter
		if (artistId != null) {
			url.append(artistId);
		} else {
			url.append("all");
		}
		url.append("/desc/0/32000/");
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
			JSAlbumResponse jsResponse = null;
			String text = response.getText();
			jsResponse = JsonUtils.safeEval(text);
			
			List<AlbumInfo> answerList = new ArrayList<AlbumInfo>(); // To be filled and returned
			JsArray<JSAlbum> albumList = jsResponse.getAlbums();
			for (int i = 0; i < albumList.length(); i++) {
				JSAlbum album = albumList.get(i);

				AlbumInfo info = new AlbumInfo(album.getId(), album.getName(), album.getSongs());
				answerList.add(info);
				// We take the opportunity to add the album to the model cache
				Controller.getAlbumCache().put(info.getId(), info);
			}

			// Only if the message arrives on time to fill the screen it was meant for
			if (targetScreen == model.getCurrentScreen()) {
				albumHandler.onAlbumChange(answerList);
			}
		} else {
			onError();
		}
	}
	
	@Override
	public void onError() {
		ErrorPopup.showError("Could not get albums");
	}

	public static AlbumInfo parseAlbumInfo(Response response) {
		String text = response.getText();
		JSAlbum album = JsonUtils.safeEval(text);
		
		return new AlbumInfo(album.getId(), album.getName(), album.getSongs());
	}
}

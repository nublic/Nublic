package com.nublic.app.music.client.datamodel.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.datamodel.Artist;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.State;
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
		url.append("/desc/0/32000");
		if (collectionId != null) {
			url.append("/");
			url.append(collectionId);
		}
		return URL.encode(url.toString());
	}

	@Override
	public void onSuccess(Response response) {
		// Set the selected collection which is being shown
		if (collectionId == null) {
			// All music
			model.setShowing();
		} else {
			model.setShowing(collectionId, true);
		}
		model.setState(State.ARTIST_ALBUMS);
		
		// Fill artists list in model with the new info
		model.clearArtistList();
		// TODO: Fake info to try
			model.addArtist(new Artist("Id1", "Joaquín Sabina", 4, 40));
			model.addArtist(new Artist("Id2", "Oasis", 4, 40));
			model.addArtist(new Artist("Id3", "Muse", 4, 40));
			model.addArtist(new Artist("Id4", "Michael Jackson", 4, 40));
		// Fake info end
		
		model.fireStateHandlers();
	}

	@Override
	public void onError() {
		onSuccess(null);
	}

}

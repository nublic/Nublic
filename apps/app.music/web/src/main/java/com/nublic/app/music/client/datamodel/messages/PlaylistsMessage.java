package com.nublic.app.music.client.datamodel.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.Playlist;
import com.nublic.util.messages.Message;

//GET /playlists
//* Get all the available playlists for this user
//* Return: [ playlist1, playlist2, ... ]
//  where:  playlist ::= { "id"   : $id
//                       , "name" : $name
//                       }

public class PlaylistsMessage extends Message {
	DataModel model;
	
	public PlaylistsMessage(DataModel model) {
		this.model = model;
	}

	@Override
	public String getURL() {
		return URL.encode(GWT.getHostPageBaseURL() + "server/playlists" );
	}

	@Override
	public void onSuccess(Response response) {
		// Fake thing to test without server
		model.addPlaylist(new Playlist("1", "Verano Mix 97"));
		model.addPlaylist(new Playlist("2", "Cena de navidad 2008"));
		model.firePlaylistsHandlers();
	}

	@Override
	public void onError() {
		// TODO: do something real
		onSuccess(null);
	}
}

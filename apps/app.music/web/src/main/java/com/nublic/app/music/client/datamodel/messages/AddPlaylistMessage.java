package com.nublic.app.music.client.datamodel.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.Playlist;
import com.nublic.util.messages.Message;

//PUT /playlists
//* Add a playlist to the system
//* :name -> name that the user gave
//* Return: the new id of the playlist
public class AddPlaylistMessage extends Message {
	String name;
	DataModel model;
	
	public AddPlaylistMessage(String name, DataModel model) {
		this.name = name;
		this.model = model;
	}
	
	@Override
	public String getURL() {
		addParam("name", name);
		return URL.encode(GWT.getHostPageBaseURL() + "server/playlists" );
	}

	@Override
	public void onSuccess(Response response) {
		model.addPlaylist(new Playlist(name + "Id", name));
		model.firePlaylistsHandlers();
	}

	@Override
	public void onError() {
		// TODO doError
		onSuccess(null);
	}

}

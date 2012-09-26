package com.nublic.app.music.client.datamodel.messages;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.controller.Controller;
import com.nublic.app.music.client.datamodel.Playlist;
import com.nublic.app.music.client.datamodel.handlers.PlaylistsChangeHandler.PlaylistsChangeEvent;
import com.nublic.app.music.client.datamodel.handlers.PlaylistsChangeHandler.PlaylistsChangeEventType;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.Message;

//PUT /playlists
//* Add a playlist to the system
//* :name -> name that the user gave
//* Return: the new id of the playlist
public class AddPlaylistMessage extends Message {
	String name;
	
	public AddPlaylistMessage(String name) {
		this.name = name;
	}
	
	@Override
	public String getURL() {
		addParam("name", name);
		addParam("songs", "");
		return URL.encode(GWT.getHostPageBaseURL() + "server/playlists");
	}

	@Override
	public void onSuccess(Response response) {
		if (response.getStatusCode() == Response.SC_OK) {
			String text = response.getText();

			List<Playlist> involvedSet = new ArrayList<Playlist>();
			Playlist p = new Playlist(text, name);
			involvedSet.add(p);
			PlaylistsChangeEvent event = new PlaylistsChangeEvent(PlaylistsChangeEventType.PLAYLISTS_ADDED, involvedSet);
			Controller.INSTANCE.getModel().firePlaylistsHandlers(event);
			Controller.INSTANCE.getModel().getPlaylistCache().put(text, p);
		} else {
			onError();
		}
	}

	@Override
	public void onError() {
		ErrorPopup.showError(Constants.I18N.addPlaylistError());
	}

}

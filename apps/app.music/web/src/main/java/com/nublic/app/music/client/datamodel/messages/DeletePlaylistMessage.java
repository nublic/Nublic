package com.nublic.app.music.client.datamodel.messages;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;
import com.nublic.app.music.client.datamodel.Controller;
import com.nublic.app.music.client.datamodel.Playlist;
import com.nublic.app.music.client.datamodel.handlers.PlaylistsChangeHandler.PlaylistsChangeEvent;
import com.nublic.app.music.client.datamodel.handlers.PlaylistsChangeHandler.PlaylistsChangeEventType;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.Message;

//DELETE /playlists
//* Delete a playlist from the system
//* :id -> id of the playlist to remove
public class DeletePlaylistMessage extends Message {
	String id;
	
	public DeletePlaylistMessage(String id) {
		this.id = id;
	}
	
	@Override
	public String getURL() {
		addParam("id", id);
		return URL.encode(GWT.getHostPageBaseURL() + "server/playlists" );
	}

	@Override
	public void onSuccess(Response response) {
		if (response.getStatusCode() == Response.SC_OK) {
			List<Playlist> involvedSet = new ArrayList<Playlist>();
			involvedSet.add(new Playlist(id, id));
			PlaylistsChangeEvent event = new PlaylistsChangeEvent(PlaylistsChangeEventType.PLAYLISTS_REMOVED, involvedSet);
			Controller.INSTANCE.getModel().firePlaylistsHandlers(event);
			Controller.INSTANCE.getModel().getPlaylistCache().remove(id);
			// reload main ui
			History.newItem("");
		} else {
			onError();
		}
	}

	@Override
	public void onError() {
		ErrorPopup.showError("Could not delete playlist");
	}

}

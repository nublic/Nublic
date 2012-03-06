package com.nublic.app.music.client.datamodel.messages;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.datamodel.Controller;
import com.nublic.app.music.client.datamodel.Playlist;
import com.nublic.app.music.client.datamodel.handlers.PlaylistsChangeHandler.PlaylistsChangeEvent;
import com.nublic.app.music.client.datamodel.handlers.PlaylistsChangeHandler.PlaylistsChangeEventType;
import com.nublic.app.music.client.datamodel.js.JSPlaylist;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.Message;

//GET /playlists
//* Get all the available playlists for this user
//* Return: [ playlist1, playlist2, ... ]
//  where:  playlist ::= { "id"   : $id
//                       , "name" : $name }
public class PlaylistsMessage extends Message {
	public PlaylistsMessage() {
	}

	@Override
	public String getURL() {
		return URL.encode(GWT.getHostPageBaseURL() + "server/playlists" );
	}

	@Override
	public void onSuccess(Response response) {
		JsArray <JSPlaylist> jsPlaylistList = null;
		
		if (response.getStatusCode() == Response.SC_OK) {
			String text = response.getText();
			jsPlaylistList = JsonUtils.safeEval(text);
			
			if (jsPlaylistList == null) {
				onError();
			} else {
				List<Playlist> receivedPlaylists = new ArrayList<Playlist>();
				for (int i = 0 ; i < jsPlaylistList.length() ; i++) {
					JSPlaylist jsPlaylist = jsPlaylistList.get(i);
					Playlist p = new Playlist(jsPlaylist.getId(), jsPlaylist.getName());
					receivedPlaylists.add(p);
					Controller.getModel().getPlaylistCache().put(jsPlaylist.getId(), p);
				}
				Controller.getModel().firePlaylistsHandlers(new PlaylistsChangeEvent(PlaylistsChangeEventType.PLAYLISTS_ADDED, receivedPlaylists));
			}
		} else {
			onError();
		}

	}

	@Override
	public void onError() {
		ErrorPopup.showError("Could not get playlist list");
	}
}

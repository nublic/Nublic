package com.nublic.app.music.client.datamodel.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.Playlist;
import com.nublic.app.music.client.datamodel.js.JSPlaylist;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.Message;

//GET /playlists
//* Get all the available playlists for this user
//* Return: [ playlist1, playlist2, ... ]
//  where:  playlist ::= { "id"   : $id
//                       , "name" : $name }
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
		JsArray <JSPlaylist> jsPlaylistList = null;
		
		if (response.getStatusCode() == Response.SC_OK) {
			String text = response.getText();
			jsPlaylistList = JsonUtils.safeEval(text);
			
			if (jsPlaylistList == null) {
				onError();
			} else {
				for (int i = 0 ; i < jsPlaylistList.length() ; i++) {
					JSPlaylist jsPlaylist = jsPlaylistList.get(i);
					model.addPlaylist(new Playlist(jsPlaylist.getId(), jsPlaylist.getName()));
				}
			}
		} else {
			onError();
		}
		
		model.firePlaylistsHandlers();
	}

	@Override
	public void onError() {
		ErrorPopup.showError("Could not get playlist list");
	}
}

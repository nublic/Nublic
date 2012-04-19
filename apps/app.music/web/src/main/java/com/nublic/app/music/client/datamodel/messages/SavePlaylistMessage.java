package com.nublic.app.music.client.datamodel.messages;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.controller.Controller;
import com.nublic.app.music.client.datamodel.Playlist;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.app.music.client.datamodel.handlers.PlaylistsChangeHandler.PlaylistsChangeEvent;
import com.nublic.app.music.client.datamodel.handlers.PlaylistsChangeHandler.PlaylistsChangeEventType;
import com.nublic.app.music.client.datamodel.handlers.SavePlaylistSuccessHandler;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.gwt.NublicLists;
import com.nublic.util.messages.Message;

//PUT /playlists
//* Add a playlist to the system
//* :name -> name that the user gave
//* :songs -> list of songs to add initially
//* Return: the new id of the playlist
public class SavePlaylistMessage extends Message {
	String name;
	List<SongInfo> songList;
	SavePlaylistSuccessHandler spsh;
	
	public SavePlaylistMessage(String name, List<SongInfo> songList, SavePlaylistSuccessHandler spsh) {
		this.name = name;
		this.songList = songList;
		this.spsh = spsh;
	}
	
	@Override
	public String getURL() {
		addParam("songs", NublicLists.joinList(songList, ","));
		addParam("name", name);
		
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
			spsh.onSaveSuccess(text);
		} else {
			onError();
		}
	}

	@Override
	public void onError() {
		ErrorPopup.showError("Could not add playlist");
	}

}

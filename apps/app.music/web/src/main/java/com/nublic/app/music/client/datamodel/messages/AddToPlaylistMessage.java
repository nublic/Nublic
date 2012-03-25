package com.nublic.app.music.client.datamodel.messages;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.gwt.NublicLists;
import com.nublic.util.messages.Message;

//PUT /playlist/:id
//* Add songs to a playlist
//* :songs -> comma-separated list of ids of songs to add at the end of the lists
public class AddToPlaylistMessage extends Message {
	String id;
	List<SongInfo> songList;
	SongInfo song;
	
	public AddToPlaylistMessage(String id, List<SongInfo> songList) {
		this.id = id;
		this.songList = songList;
		this.song = null;
	}
	
	public AddToPlaylistMessage(String id, SongInfo song) {
		this.id = id;
		this.songList = null;
		this.song = song;
	}
	
	@Override
	public String getURL() {
		if (songList != null) {
			addParam("songs", NublicLists.joinList(songList, ","));
		} else {
			addParam("songs", song.getId());
		}
		
		return URL.encode(GWT.getHostPageBaseURL() + "server/playlist/" + id);
	}

	@Override
	public void onSuccess(Response response) {
		if (response.getStatusCode() == Response.SC_OK) {
//			Playlist targetPlaylist = Controller.INSTANCE.getModel().getPlaylistCache().get(id);
		} else {
			onError();
		}
	}

	@Override
	public void onError() {
		ErrorPopup.showError("Could not add song to playlist");
	}

}

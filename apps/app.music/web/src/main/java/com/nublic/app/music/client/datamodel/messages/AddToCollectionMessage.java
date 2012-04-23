package com.nublic.app.music.client.datamodel.messages;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.gwt.NublicLists;
import com.nublic.util.messages.Message;

//PUT /collection/:id
//* Add songs to a collection
//* :songs -> comma-separated list of ids of songs to add
public class AddToCollectionMessage extends Message {
	String id;
	List<SongInfo> songList;
	SongInfo song;
	
	public AddToCollectionMessage(String id, List<SongInfo> songList) {
		this.id = id;
		this.songList = songList;
		this.song = null;
	}
	
	public AddToCollectionMessage(String id, SongInfo song) {
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
		
		return URL.encode(GWT.getHostPageBaseURL() + "server/collection/" + id);
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
		ErrorPopup.showError("Could not add song to collection");
	}

}

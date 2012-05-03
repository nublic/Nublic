package com.nublic.app.music.client.ui.song;

import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.controller.Controller;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.app.music.client.datamodel.handlers.AddAtEndButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.EditButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.PlayButtonHandler;

public abstract class AlbumSongList extends SongList {
	String albumId;
	String artistId;
	String collectionId;
	
	public AlbumSongList(String albumId, String artistId, String collectionId, int numberOfSongs, Widget scrollPanel) {
		super(numberOfSongs, scrollPanel);
		this.albumId = albumId;
		this.artistId = artistId;
		this.collectionId = collectionId;
	}
	
	@Override
	public void askForsongs(int from, int to) {
		Controller.INSTANCE.getModel().askForSongs(from, to, albumId, artistId, collectionId, songHandler);
	}
	
	// +++ Handlers for buttons +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	protected class MyAddAtEndHandler implements AddAtEndButtonHandler {
		SongInfo song;
		public MyAddAtEndHandler(SongInfo s) {
			this.song = s;
		}
		@Override
		public void onAddAtEnd() {
			Controller.INSTANCE.addAtEndOfPlayingPlaylist(song);
		}
	}
	
	protected class MyPlayHandler implements PlayButtonHandler {
		SongInfo song;
		public MyPlayHandler(SongInfo s) {
			this.song = s;
		}
		@Override
		public void onPlay() {
			Controller.INSTANCE.play(song);
		}
	}
	
	protected class MyEditHandler implements EditButtonHandler {
		@Override
		public void onEdit() {
			// TODO: Edit
			
		}
	}
}

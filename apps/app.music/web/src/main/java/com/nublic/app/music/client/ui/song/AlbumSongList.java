package com.nublic.app.music.client.ui.song;

import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.datamodel.Controller;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.app.music.client.datamodel.handlers.AddAtEndButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.EditButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.PlayButtonHandler;

public class AlbumSongList extends SongList {
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
		Controller.getModel().askForSongs(from, to, albumId, artistId, collectionId, songHandler);
	}

	@Override
	protected void prepareGrid() {
		grid.resize(numberOfSongs, 2);
		grid.getColumnFormatter().setWidth(0, Constants.FIRST_COLUMN_WIDTH);		
	}
	
	@Override
	public void setSong(int row, SongInfo s) {
		setTrackNumber(row, 0, s.getTrack());									   // Column 0
		setTitleLenght(row, 1, s,												   // Column 1
			new MyAddAtEndHandler(s), new MyPlayHandler(s), new MyEditHandler());  // (Column 1)
	}
	
	// +++ Handlers for buttons +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	protected class MyAddAtEndHandler implements AddAtEndButtonHandler {
		SongInfo song;
		public MyAddAtEndHandler(SongInfo s) {
			this.song = s;
		}
		@Override
		public void onAddAtEnd() {
			Controller.addAtEndOfCurrentPlaylist(song);
		}
	}
	
	protected class MyPlayHandler implements PlayButtonHandler {
		SongInfo song;
		public MyPlayHandler(SongInfo s) {
			this.song = s;
		}
		@Override
		public void onPlay() {
			Controller.play(song);
		}
	}
	
	protected class MyEditHandler implements EditButtonHandler {
		@Override
		public void onEdit() {
			// TODO: Edit
			
		}
	}
}

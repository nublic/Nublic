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
		Controller.INSTANCE.getModel().askForSongs(from, to, albumId, artistId, collectionId, songHandler);
	}

	@Override
	protected void prepareGrid() {
		grid.resize(numberOfSongs, 3);
		grid.getColumnFormatter().setWidth(0, Constants.GRABBER_WIDTH);
		grid.getColumnFormatter().setWidth(1, Constants.TRACK_NUMBER_WIDTH);
	}
	
	@Override
	public void setSong(int row, SongInfo s) {
		setGrabber(row, 0, s);														   // Column 0
		setTrackNumber(row, 1, s.getTrack());									   // Column 1
		setTitleLenght(row, 2, s,												   // Column 2
			new MyAddAtEndHandler(s), new MyPlayHandler(s), new MyEditHandler());  // (Column 3)
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

package com.nublic.app.music.client.ui.song;

import com.bramosystems.oss.player.core.event.client.PlayStateEvent;
import com.bramosystems.oss.player.core.event.client.PlayStateHandler;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.datamodel.Controller;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.app.music.client.datamodel.handlers.DeleteButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.PlayButtonHandler;

public class PlaylistSongList extends SongList implements PlayStateHandler {
	String playlistId;
	int playingIndex = -1;
	
	public PlaylistSongList(String playlistId, int numberOfSongs, Widget scrollPanel) {
		super(numberOfSongs, scrollPanel);
		this.playlistId = playlistId;
		
		Controller.getPlayer().addPlayStateHandler(this);
		this.onPlayStateChanged(Controller.getPlayer().getLastEvent());
	}

	@Override
	public void askForsongs(int from, int to) {
		Controller.getModel().askForPlaylistSongs(from, to, playlistId, songHandler);
	}
	
	@Override
	protected void prepareGrid() {
		grid.resize(numberOfSongs, 6);
	}

	@Override
	public void setSong(int row, SongInfo s) {
		setButtons(row, 0, s, new MyPlayHandler(row), new MyDeleteHandler(row));	// Column 0
		setTrackNumber(row, 1, s.getTrack());										// Column 1
		setTitle(row, 2, s.getTitle());												// Column 2
		setLenght(row, 3, s.getFormattedLength());									// Column 3
		setAlbum(row, 4, s);														// Column 4
		setArtist(row, 5, s);														// Column 5
	}

	@Override
	public void onPlayStateChanged(PlayStateEvent event) {
		if (event != null && Controller.getPlayingPlaylistId().equals(playlistId)) {
			switch (event.getPlayState()) {
			case Paused:
				setSongPaused(event.getItemIndex());
				break;
			case Started:
				setSongPlaying(event.getItemIndex());
				break;
			case Stopped:
				unmark(event.getItemIndex());
				break;
			case Finished:
				unmark(playingIndex);
				playingIndex = -1;
				break;
			}
		}
	}

	public void setSongPaused(int songIndex) {
		if (songIndex != playingIndex) {
			unmark(playingIndex);
		}
		playingIndex = songIndex;
		grid.getRowFormatter().addStyleName(playingIndex, "songselected");
	}
	
	public void setSongPlaying(int songIndex) {
		if (songIndex != playingIndex) {
			unmark(playingIndex);
		}
		playingIndex = songIndex;
		grid.getRowFormatter().addStyleName(playingIndex, "songselected");
	}

	private void unmark(int songIndex) {
		if (playingIndex != -1) {
			grid.getRowFormatter().removeStyleName(playingIndex, "songselected");
		}
	}

	// +++ Handlers for buttons +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	protected class MyPlayHandler implements PlayButtonHandler {
		int row;
		public MyPlayHandler(int row) {
			this.row = row;
		}
		@Override
		public void onPlay() {
			Controller.play(row, playlistId);
		}
	}
	
	protected class MyDeleteHandler implements DeleteButtonHandler {
		int row;
		public MyDeleteHandler(int row) {
			this.row = row;
		}
		@Override
		public void onDelete() {
			// TODO: delete from playlist
		}
	}
	
}

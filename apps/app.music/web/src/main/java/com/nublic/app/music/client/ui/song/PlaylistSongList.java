package com.nublic.app.music.client.ui.song;

import com.bramosystems.oss.player.core.event.client.PlayStateEvent;
import com.bramosystems.oss.player.core.event.client.PlayStateHandler;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.datamodel.Controller;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.app.music.client.datamodel.handlers.DeleteButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.PlayButtonHandler;
import com.nublic.app.music.client.ui.ButtonLine;
import com.nublic.app.music.client.ui.dnd.Draggable;

public class PlaylistSongList extends SongList implements PlayStateHandler {
	String playlistId;
	int playingIndex = -1;
	
	public PlaylistSongList(String playlistId, int numberOfSongs, Widget scrollPanel) {
		super(numberOfSongs, scrollPanel);
		this.playlistId = playlistId;
		
		Controller.INSTANCE.getPlayer().addPlayStateHandler(this);
		this.onPlayStateChanged(Controller.INSTANCE.getPlayer().getLastEvent());
		
		createDropController();
	}
	
	// To handle drag and drop
	private void createDropController() {
		Controller.INSTANCE.createCenterDropController(grid, playlistId);
	}

	@Override
	public void askForsongs(int from, int to) {
		Controller.INSTANCE.getModel().askForPlaylistSongs(from, to, playlistId, songHandler);
	}
	
	@Override
	protected void prepareGrid() {
		grid.resize(numberOfSongs, 7);
	}

	@Override
	public void setSong(int row, SongInfo s) {
		setGrabber(row, 0, s);															// Column 0
		setButtons(row, 1, s, new MyPlayHandler(row), new MyDeleteHandler(row));	// Column 1
		setTrackNumber(row, 2, s.getTrack());										// Column 2
		setTitle(row, 3, s.getTitle());												// Column 3
		setLenght(row, 4, s.getFormattedLength());									// Column 4
		setAlbum(row, 5, s);														// Column 5
		setArtist(row, 6, s);														// Column 6
	}

	@Override
	public void onPlayStateChanged(PlayStateEvent event) {
		if (event != null && Controller.INSTANCE.isBeingPlayed(playlistId)) {
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
			Controller.INSTANCE.play(row, playlistId);
		}
	}
	
	protected class MyDeleteHandler implements DeleteButtonHandler {
		int row;
		public MyDeleteHandler(int row) {
			this.row = row;
		}
		@Override
		public void onDelete() {
			// If the song removed is being played
			if (playingIndex == row && Controller.INSTANCE.isBeingPlayed(playlistId)) {
				Controller.INSTANCE.getPlayer().stopMedia();
			}
			// Remove from server
			Controller.INSTANCE.getModel().removeFromPlaylist(playlistId, row, new DeleteButtonHandler() {
				@Override
				public void onDelete() {
					// Remove from interface
					grid.removeRow(row);
					// If we are being played
					if (Controller.INSTANCE.isBeingPlayed(playlistId)) {
						Controller.INSTANCE.getPlayer().nublicRemoveFromPlaylist(row);
					}
					rearrangeRows(row, grid.getRowCount() -1);
				}
			});
		}
	}
	
	public void moveRows(int from, int to) {
		// Insert new row
		grid.insertRow(to);
		grid.getRowFormatter().getElement(to).addClassName("translucidPanel");
		
		int newFrom = to < from ? from + 1 : from;
		
		// Copy the old one to the new row
		for (int i = 0; i < grid.getColumnCount(); i++) {
			grid.setWidget(to, i, grid.getWidget(newFrom, i));
		}
		
		// Remove old copy
		grid.removeRow(newFrom);
		
		int rearrangePoint = newFrom < to ? newFrom : to;
		rearrangeRows(rearrangePoint, grid.getRowCount() -1);
	}
	
	public void rearrangeRows(int from, int to) {
		for (int i = from; i <= to ; i++) {
			Widget w = grid.getWidget(i, 0);
			if (w instanceof SongLocalizer) {
				((SongLocalizer)w).setPosition(i);
			} else {
				((Draggable)w).setRow(i);
				ButtonLine bl = (ButtonLine)grid.getWidget(i, 1);
				bl.setPlayButtonHandler(new MyPlayHandler(i));
				bl.setDeleteButtonHandler(new MyDeleteHandler(i));
			}
		}
	}
	
}

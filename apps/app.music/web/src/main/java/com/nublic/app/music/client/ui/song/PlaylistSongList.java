package com.nublic.app.music.client.ui.song;

import com.bramosystems.oss.player.core.event.client.PlayStateEvent;
import com.bramosystems.oss.player.core.event.client.PlayStateEvent.State;
import com.bramosystems.oss.player.core.event.client.PlayStateHandler;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.controller.Controller;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.app.music.client.datamodel.handlers.DeleteButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.PlayButtonHandler;
import com.nublic.app.music.client.ui.ButtonLine;
import com.nublic.app.music.client.ui.dnd.DraggableSong;

public class PlaylistSongList extends SongList implements PlayStateHandler {
	String playlistId;
	int playingIndex = -1;
	
	public PlaylistSongList(String playlistId, int numberOfSongs, Panel scrollPanel) {
		super(numberOfSongs, scrollPanel);
		this.playlistId = playlistId;
		
		Controller.INSTANCE.getPlayer().addPlayStateHandler(this);
		this.onPlayStateChanged(Controller.INSTANCE.getPlayer().getState(), Controller.INSTANCE.getPlayer().getPlaylistIndex());
		
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
		grid.getColumnFormatter().setWidth(0, Constants.GRABBER_WIDTH);
		grid.getColumnFormatter().setWidth(1, Constants.BUTTONS_WIDTH);
		grid.getColumnFormatter().setWidth(2, Constants.TRACK_NUMBER_WIDTH);
	}

	@Override
	public void setSong(int row, SongInfo s) {
		grid.getCellFormatter().setHeight(row, 0, Constants.TABLE_CELL_HEIGHT);
		setGrabber(row, 0, s);														// Column 0
		setButtons(row, 1, s, new MyPlayHandler(row), new MyDeleteHandler(row));	// Column 1
		setTrackNumber(row, 2, s.getTrack());										// Column 2
		setTitle(row, 3, s.getTitle());												// Column 3
		setLenght(row, 4, s.getFormattedLength());									// Column 4
		setAlbum(row, 5, s);														// Column 5
		setArtist(row, 6, s);														// Column 6
	}

	@Override
	public void onPlayStateChanged(PlayStateEvent event) {
		onPlayStateChanged(event.getPlayState(), event.getItemIndex());
	}
	
	public void onPlayStateChanged(State s, int index) {
		if (s != null && Controller.INSTANCE.isBeingPlayed(playlistId)) {
			switch (s) {
			case Paused:
				setSongPaused(index);
				break;
			case Started:
				setSongPlaying(index);
				break;
			case Stopped:
				unmark(index);
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
					updateRangesFromDelete(row);
					rearrangeRows(row, grid.getRowCount() -1);
					updateEmptyness();
				}
			});
		}
	}
	
	public void moveRows(int from, int to) {
		// get old style to apply to new one
		String oldClassName = grid.getRowFormatter().getElement(from).getClassName();

		// Insert new row
		grid.insertRow(to);
		grid.getRowFormatter().getElement(to).setClassName(oldClassName);
		grid.getCellFormatter().setHeight(to, 0, Constants.TABLE_CELL_HEIGHT);
		
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
				((DraggableSong)w).setRow(i);
				ButtonLine bl = (ButtonLine)grid.getWidget(i, 1);
				bl.setPlayButtonHandler(new MyPlayHandler(i));
				bl.setDeleteButtonHandler(new MyDeleteHandler(i));
			}
		}
		// Keep playingIndex updated
		playingIndex = Controller.INSTANCE.getPlayer().getPlaylistIndex();
	}
	
}

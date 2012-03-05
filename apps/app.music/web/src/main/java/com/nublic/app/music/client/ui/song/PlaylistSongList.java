package com.nublic.app.music.client.ui.song;

import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.SongInfo;

public class PlaylistSongList extends SongList {
	String playlistId = null;
	
	public PlaylistSongList(DataModel model, String playlistId, int numberOfSongs, Widget scrollPanel) {
		super(model, numberOfSongs, scrollPanel);
		this.playlistId = playlistId;
	}

	@Override
	public void askForsongs(int from, int to) {
		model.askForPlaylistSongs(from, to, playlistId, songHandler);
	}
	
	@Override
	protected void prepareGrid() {
		grid.resize(numberOfSongs, 6);
	}

	@Override
	public void setSong(int row, SongInfo s) {
		setButtons(row, 0, s);						// Column 0
		setTrackNumber(row, 1, s.getTrack());		// Column 1
		setTitle(row, 2, s.getTitle());				// Column 2
		setLenght(row, 3, s.getFormattedLength());	// Column 3
		setAlbum(row, 4, s);						// Column 4
		setArtist(row, 5, s);						// Column 5
	}
	
	
}

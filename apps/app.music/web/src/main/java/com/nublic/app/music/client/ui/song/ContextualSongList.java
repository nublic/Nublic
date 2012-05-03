package com.nublic.app.music.client.ui.song;

import com.google.gwt.user.client.ui.Panel;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.datamodel.SongInfo;

public class ContextualSongList extends AlbumSongList {
	public ContextualSongList(String albumId, String artistId, String collectionId, int numberOfSongs, Panel scrollPanel) {
		super(albumId, artistId, collectionId, numberOfSongs, scrollPanel);
	}
	
	@Override
	protected void prepareGrid() {
		grid.resize(numberOfSongs, 3);
		grid.getColumnFormatter().setWidth(0, Constants.GRABBER_WIDTH);
		grid.getColumnFormatter().setWidth(1, Constants.TRACK_NUMBER_WIDTH);
	}
	
	@Override
	public void setSong(int row, SongInfo s) {
		grid.getCellFormatter().setHeight(row, 0, Constants.TABLE_CELL_HEIGHT);
		setGrabber(row, 0, s);													   // Column 0
		setTrackNumber(row, 1, s.getTrack());									   // Column 1
		setTitleLenght(row, 2, s,												   // Column 2
			new MyAddAtEndHandler(s), new MyPlayHandler(s),	new MyEditHandler(),
			collectionId == null ? null : new MyDeleteHandler(s, row));
	}

}

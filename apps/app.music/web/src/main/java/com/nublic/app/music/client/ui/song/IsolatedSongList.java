package com.nublic.app.music.client.ui.song;

import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.datamodel.SongInfo;

public class IsolatedSongList extends AlbumSongList {
	public IsolatedSongList(String albumId, String artistId, String collectionId, int numberOfSongs, Widget scrollPanel) {
		super(albumId, artistId, collectionId, numberOfSongs, scrollPanel);
	}
	
	@Override
	protected void prepareGrid() {
		grid.resize(numberOfSongs, 4);
		grid.getColumnFormatter().setWidth(0, Constants.GRABBER_WIDTH);
	}
	
	@Override
	public void setSong(int row, SongInfo s) {
		grid.getCellFormatter().setHeight(row, 0, Constants.TABLE_CELL_HEIGHT);
		setGrabber(row, 0, s);													   // Column 0
		setTitleLenght(row, 1, s,												   // Column 1
			new MyAddAtEndHandler(s), new MyPlayHandler(s), new MyEditHandler());  // (Column 1.5)
		setArtist(row, 2, s);													   // Column 2
		setAlbum(row, 3, s);													   // Column 3
	}

}

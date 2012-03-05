package com.nublic.app.music.client.ui.song;

import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.SongInfo;

public class AlbumSongList extends SongList {
	String albumId;
	String artistId;
	String collectionId;
	
	public AlbumSongList(DataModel model, String albumId, String artistId, String collectionId, int numberOfSongs, Widget scrollPanel) {
		super(model, numberOfSongs, scrollPanel);
		this.albumId = albumId;
		this.artistId = artistId;
		this.collectionId = collectionId;
	}
	
	@Override
	public void askForsongs(int from, int to) {
		model.askForSongs(from, from, albumId, artistId, collectionId, songHandler);
	}

	@Override
	protected void prepareGrid() {
		grid.resize(numberOfSongs, 2);
		grid.getColumnFormatter().setWidth(0, Constants.FIRST_COLUMN_WIDTH);		
	}
	
	@Override
	public void setSong(int row, SongInfo s) {
		setTrackNumber(row, 0, s.getTrack()); 		// Column 0
		setTitleLenght(row, 1, s);					// Column 1
	}
}

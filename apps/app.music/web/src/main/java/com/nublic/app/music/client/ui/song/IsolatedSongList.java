package com.nublic.app.music.client.ui.song;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.app.music.client.ui.ButtonLine;
import com.nublic.app.music.client.ui.dnd.DraggableSong;

public class IsolatedSongList extends AlbumSongList {
	public IsolatedSongList(String albumId, String artistId, String collectionId, int numberOfSongs, Panel scrollPanel) {
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
			new MyAddAtEndHandler(s), new MyPlayHandler(s),	new MyEditHandler(),
			collectionId == null ? null : new MyDeleteHandler(s, row));
		setArtist(row, 2, s);													   // Column 2
		setAlbum(row, 3, s);													   // Column 3
	}
	
	@Override
	public void rearrangeRows(int from, int to) {
		for (int i = from; i <= to ; i++) {
			Widget w = grid.getWidget(i, 0);
			if (w instanceof SongLocalizer) {
				((SongLocalizer)w).setPosition(i);
			} else {
				((DraggableSong)w).setRow(i);
				SongInfo s = ((DraggableSong)w).getSong();
				ButtonLine bl = (ButtonLine)((HorizontalPanel)grid.getWidget(i, 1)).getWidget(1);
				bl.setDeleteButtonHandler(new MyDeleteHandler(s, i));
			}
		}
	}

}

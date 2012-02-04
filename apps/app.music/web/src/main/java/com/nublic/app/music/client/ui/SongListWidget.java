package com.nublic.app.music.client.ui;

import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.RangeChangeEvent.Handler;
import com.nublic.app.music.client.datamodel.Song;

public class SongListWidget extends CellList<Song> {
	static SongCell songCell = new SongCell();
	
	public SongListWidget() {
		super(songCell);
		
		addRangeChangeHandler(new Handler() {
			@Override
			public void onRangeChange(RangeChangeEvent event) {
				
			}
		});
	}
}

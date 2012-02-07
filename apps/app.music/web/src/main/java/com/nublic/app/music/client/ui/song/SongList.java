package com.nublic.app.music.client.ui.song;

import java.util.EnumSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Grid;
import com.nublic.app.music.client.datamodel.Album;
import com.nublic.app.music.client.datamodel.Song;
import com.nublic.app.music.client.datamodel.handlers.SongsChangeHandler;
import com.nublic.app.music.client.datamodel.messages.SongMessage;
import com.nublic.app.music.client.ui.ButtonLine;
import com.nublic.app.music.client.ui.ButtonLineParam;
import com.nublic.util.messages.DefaultComparator;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceIgnorer;

public class SongList extends Composite implements ScrollHandler {
	private static SongListUiBinder uiBinder = GWT.create(SongListUiBinder.class);
	interface SongListUiBinder extends UiBinder<Widget, SongList> { }
	
	@UiField Grid grid;
	Album album;
	SequenceIgnorer<Message> sendHelper = new SequenceIgnorer<Message>(DefaultComparator.INSTANCE);

	public SongList(Album a) {
		// TODO: pass scroll panel in which we are in to handle lazy loading
		// And number of songs
		initWidget(uiBinder.createAndBindUi(this));
		album = a;
		
		album.prepareToAddSongs();
		album.addSongsChangeHandler(new SongsChangeHandler() {
			@Override
			public void onSongsChange(int from, int to) {
				for (int i = from; i <= to; i++) {
					setSong(i, album.getSong(i));
				}
			}
		});
		
		//         |
		// prepare v fake widgets which know if are being shown to be replaced onScroll 
//		album.getNumberOfSongs();
	}
	
	public SongList(String artistId, String albumId, String collectionId, int numberOfSongs) {
	}

	@Override
	public void onScroll(ScrollEvent event) {
		
		// TODO: get from and to limits;
		SongMessage sm = new SongMessage(album);
		sendHelper.send(sm, RequestBuilder.GET);
	}
	
	public void setSong(int row, Song s) {
		Label titleLabel = new Label(s.getTitle());
		grid.setWidget(row, 0, titleLabel);
		grid.setWidget(row, 1, new ButtonLine(EnumSet.of(ButtonLineParam.PLAY, ButtonLineParam.EDIT)));
	}

}

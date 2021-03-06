package com.nublic.app.music.client.ui.dnd;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.datamodel.SongInfo;

public class DraggableSong extends Composite implements HasMouseDownHandlers {
	private static DraggableSongUiBinder uiBinder = GWT.create(DraggableSongUiBinder.class);
	interface DraggableSongUiBinder extends UiBinder<Widget, DraggableSong> { }
	
	int row;
	SongInfo song;
	
	public DraggableSong(int row, SongInfo song) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.row = row;
		this.song = song;
	}

	public int getRow() { return row; }
	public void setRow(int row) { this.row = row; }
	public SongInfo getSong() { return song; }
	public void setSong(SongInfo song) { this.song = song; }

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}

}

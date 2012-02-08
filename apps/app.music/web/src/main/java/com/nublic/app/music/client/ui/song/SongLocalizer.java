package com.nublic.app.music.client.ui.song;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class SongLocalizer extends Composite {
	private static SongLocalizerUiBinder uiBinder = GWT.create(SongLocalizerUiBinder.class);
	interface SongLocalizerUiBinder extends UiBinder<Widget, SongLocalizer> { }

	int position;
	
	public SongLocalizer(int position) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.position = position;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
	public boolean isInRange(int top, int bottom) {
		int myTop = getAbsoluteTop();
		int myBottom = myTop + getOffsetHeight();
		return (myBottom > top) && (myTop < bottom);
	}

}

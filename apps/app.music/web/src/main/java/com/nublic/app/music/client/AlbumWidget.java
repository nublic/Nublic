package com.nublic.app.music.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Image;

public class AlbumWidget extends Composite {

	private static AlbumWidgetUiBinder uiBinder = GWT
			.create(AlbumWidgetUiBinder.class);
	@UiField Label albumTitle;
	@UiField Image albumIm;

	interface AlbumWidgetUiBinder extends UiBinder<Widget, AlbumWidget> {
	}

	public AlbumWidget(Album value) {	
		
		initWidget(uiBinder.createAndBindUi(this));
		albumTitle.setText(value.getAlbumTitle());
		albumIm.setResource(value.getAlbumImage());
	}

}

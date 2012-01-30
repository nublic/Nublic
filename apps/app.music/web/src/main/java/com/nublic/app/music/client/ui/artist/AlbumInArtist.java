package com.nublic.app.music.client.ui.artist;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Image;
import com.nublic.app.music.client.datamodel.Album;

public class AlbumInArtist extends Composite {
	private static AlbumInArtistUiBinder uiBinder = GWT.create(AlbumInArtistUiBinder.class);
	interface AlbumInArtistUiBinder extends UiBinder<Widget, AlbumInArtist> { }
	
	@UiField Label albumNameLabel;
	@UiField Image albumImage;

	public AlbumInArtist(Album a) {
		initWidget(uiBinder.createAndBindUi(this));
	}

}

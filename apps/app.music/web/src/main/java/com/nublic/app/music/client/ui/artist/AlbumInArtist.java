package com.nublic.app.music.client.ui.artist;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Image;
import com.nublic.app.music.client.datamodel.Album;

//GET /album-art/:album-id
//* Retrieve the image associated with an album
//* Return: the raw image data

public class AlbumInArtist extends Composite {
	private static AlbumInArtistUiBinder uiBinder = GWT.create(AlbumInArtistUiBinder.class);
	interface AlbumInArtistUiBinder extends UiBinder<Widget, AlbumInArtist> { }
	
	@UiField Label albumNameLabel;
	@UiField Image albumImage;

	public AlbumInArtist(Album a) {
		initWidget(uiBinder.createAndBindUi(this));
		
		// building imageUrl as /album-art/:album-id
		StringBuilder imageUrl = new StringBuilder();
		imageUrl.append(GWT.getHostPageBaseURL());
		imageUrl.append("server/album-art/");
		imageUrl.append(a.getId());
		
		albumImage.setUrl(URL.encode(imageUrl.toString()));
		albumNameLabel.setText(a.getName());
	}

}

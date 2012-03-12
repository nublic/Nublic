package com.nublic.app.music.client.ui.artist;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.Resources;
import com.nublic.app.music.client.datamodel.AlbumInfo;

//GET /album-art/:album-id
//* Retrieve the image associated with an album
//* Return: the raw image data

public class AlbumInArtist extends Composite {
	private static AlbumInArtistUiBinder uiBinder = GWT.create(AlbumInArtistUiBinder.class);
	interface AlbumInArtistUiBinder extends UiBinder<Widget, AlbumInArtist> { }
	
	@UiField Hyperlink albumNameLabel;
	@UiField Image albumImage;
	AlbumInfo album;
	String collectionId;

	public AlbumInArtist(AlbumInfo a, String collectionId) {
		initWidget(uiBinder.createAndBindUi(this));
		this.collectionId = collectionId;
		this.album = a;

		setImage();
		albumNameLabel.setText(a.getName());
		setClickTarget();
	}
	
	private void setImage() {
		// building imageUrl as /album-art/:album-id
		StringBuilder imageUrl = new StringBuilder();
		imageUrl.append(GWT.getHostPageBaseURL());
		imageUrl.append("server/album-art/");
		imageUrl.append(album.getId());
		imageUrl.append(".png");
		
		albumImage.setUrl(URL.encode(imageUrl.toString()));
		albumImage.addErrorHandler(new ErrorHandler() {
			@Override
			public void onError(ErrorEvent event) {
				albumImage.setResource(Resources.INSTANCE.album());
			}
		});
	}

	private void setClickTarget() {
		StringBuilder target = new StringBuilder();		
		if (collectionId != null) {
			target.append(Constants.PARAM_COLLECTION);
			target.append("=");
			target.append(collectionId);
			target.append("&");
		}
		target.append(Constants.PARAM_ALBUM);
		target.append("=");
		target.append(album.getId());
		albumNameLabel.setTargetHistoryToken(target.toString());
	}

}

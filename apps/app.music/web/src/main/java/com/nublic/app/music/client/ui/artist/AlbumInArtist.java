package com.nublic.app.music.client.ui.artist;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Resources;
import com.nublic.app.music.client.datamodel.AlbumInfo;
import com.nublic.app.music.client.datamodel.Controller;
import com.nublic.app.music.client.ui.dnd.HasAlbumInfo;

public class AlbumInArtist extends Composite implements HasAlbumInfo, HasMouseDownHandlers {
	private static AlbumInArtistUiBinder uiBinder = GWT.create(AlbumInArtistUiBinder.class);
	interface AlbumInArtistUiBinder extends UiBinder<Widget, AlbumInArtist> { }
	
	@UiField Hyperlink albumNameLabel;
	@UiField Image albumImage;
	AlbumInfo album;
	String collectionId;
	String inArtist;

	public AlbumInArtist(AlbumInfo a, String inArtist, String collectionId) {
		initWidget(uiBinder.createAndBindUi(this));
		this.collectionId = collectionId;
		this.album = a;
		this.inArtist = inArtist;

		setImage();
		albumNameLabel.setText(a.getName());
		setClickTarget();

		Controller.INSTANCE.makeDraggable(this);
	}

	private void setImage() {
		albumImage.addErrorHandler(new ErrorHandler() {
			@Override
			public void onError(ErrorEvent event) {
				albumImage.setResource(Resources.INSTANCE.album());
			}
		});
		albumImage.setUrl(album.getImageUrl());
	}

	private void setClickTarget() {
		final String target = album.getTargetHistoryToken(collectionId);
		albumNameLabel.setTargetHistoryToken(target);	// To name label
		albumImage.addClickHandler(new ClickHandler() { // To art image
			@Override
			public void onClick(ClickEvent event) {
				History.newItem(target);
			}
		});
	}

	@Override
	public String getAlbumId() {
		return album.getId();
	}

	@Override
	public String getArtistId() {
		return inArtist;
	}

	@Override
	public String getCollectionId() {
		return collectionId;
	}

	@Override
	public int getNumberOfSongs() {
		return album.getNumberOfSongs();
	}
	
	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}

}

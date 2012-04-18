package com.nublic.app.music.client.ui.album;

import java.util.EnumSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Resources;
import com.nublic.app.music.client.datamodel.AlbumInfo;
import com.nublic.app.music.client.datamodel.Controller;
import com.nublic.app.music.client.datamodel.handlers.AddAtEndButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.EditButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.PlayButtonHandler;
import com.nublic.app.music.client.ui.ButtonLine;
import com.nublic.app.music.client.ui.ButtonLineParam;
import com.nublic.app.music.client.ui.song.AlbumSongList;

public class AlbumWidget extends Composite {
	private static AlbumWidgetUiBinder uiBinder = GWT.create(AlbumWidgetUiBinder.class);
	interface AlbumWidgetUiBinder extends UiBinder<Widget, AlbumWidget> { }

	@UiField HorizontalPanel labelAndButtonsPanel;
	@UiField Hyperlink albumNameLabel;
	@UiField AlbumImagePanel albumImagePanel; // For making it draggable
	@UiField Image albumImage;
	@UiField FlowPanel songsPanel;
	AlbumInfo album;
	String artistId;
	String collectionId;
	
	public AlbumWidget(AlbumInfo a, String artistId, String collectionId, Widget inPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.album = a;
		this.artistId = artistId;
		this.collectionId = collectionId;

		setImage();

		albumNameLabel.setText(album.getName());
		setClickTarget(collectionId);

		// Add button line
		ButtonLine b = new ButtonLine(EnumSet.of(ButtonLineParam.EDIT,
												 ButtonLineParam.ADD_AT_END,
												 ButtonLineParam.PLAY));
		setEditButtonHandler(b);
		setAddAtEndButtonHandler(b);
		setPlayButtonHandler(b);
		labelAndButtonsPanel.add(b);
		
		// Add song list
		songsPanel.add(new AlbumSongList(album.getId(), artistId, collectionId, album.getNumberOfSongs(), inPanel));
		
		// Make image draggable
		albumImagePanel.setProperties(album.getId(), artistId, collectionId, album.getNumberOfSongs());
		Controller.INSTANCE.makeDraggable(albumImagePanel);
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

	private void setClickTarget(String collectionId) {
		final String target = album.getTargetHistoryToken(collectionId);
		albumNameLabel.setTargetHistoryToken(target);	// To name label
		albumImage.addClickHandler(new ClickHandler() { // To art image
			@Override
			public void onClick(ClickEvent event) {
				History.newItem(target);
			}
		});
	}
	
	// Handlers for button line
	private void setEditButtonHandler(ButtonLine b) {
		b.setEditButtonHandler(new EditButtonHandler() {
			@Override
			public void onEdit() {
				// TODO: Edit
			}
		});
	}

	private void setAddAtEndButtonHandler(ButtonLine b) {
		b.setAddAtEndButtonHandler(new AddAtEndButtonHandler() {
			@Override
			public void onAddAtEnd() {
				Controller.INSTANCE.addAtEnd(artistId, album.getId(), collectionId);
			}
		});
	}

	private void setPlayButtonHandler(ButtonLine b) {
		b.setPlayButtonHandler(new PlayButtonHandler() {
			@Override
			public void onPlay() {
				Controller.INSTANCE.play(artistId, album.getId(), collectionId);
			}
		});
	}

}

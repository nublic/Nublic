package com.nublic.app.music.client.ui.album;

import java.util.EnumSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.Resources;
import com.nublic.app.music.client.Utils;
import com.nublic.app.music.client.controller.Controller;
import com.nublic.app.music.client.datamodel.AlbumInfo;
import com.nublic.app.music.client.datamodel.ArtistInfo;
import com.nublic.app.music.client.datamodel.handlers.AddAtEndButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.DeleteButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.EditButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.PlayButtonHandler;
import com.nublic.app.music.client.ui.ButtonLine;
import com.nublic.app.music.client.ui.ButtonLineParam;
import com.nublic.app.music.client.ui.ButtonType;
import com.nublic.app.music.client.ui.song.ContextualSongList;
import com.nublic.util.cache.CacheHandler;
import com.nublic.util.widgets.ImageHelper;

public class AlbumWidget extends Composite {
	private static AlbumWidgetUiBinder uiBinder = GWT.create(AlbumWidgetUiBinder.class);
	interface AlbumWidgetUiBinder extends UiBinder<Widget, AlbumWidget> { }

	@UiField HorizontalPanel labelAndButtonsPanel;
	@UiField Hyperlink albumNameLabel;
	@UiField AlbumImagePanel albumImagePanel; // For making it draggable
	@UiField Image albumImage;
	@UiField FlowPanel songsPanel;
	@UiField HorizontalPanel artistsOfAlbumPanel;
	@UiField Label artistsOfAlbumLabel;
	AlbumInfo album;
	String artistId;
	String collectionId;
	
	public AlbumWidget(AlbumInfo a, String artistId, String collectionId, Panel inPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.album = a;
		this.artistId = artistId;
		this.collectionId = collectionId;

		setImage();

		// Album title
		albumNameLabel.setText(album.getName());
		setClickTarget(collectionId);

		// Add button line
		EnumSet<ButtonLineParam> buttonSet = EnumSet.of(ButtonLineParam.EDIT,
				 ButtonLineParam.ADD_AT_END,
				 ButtonLineParam.PLAY);
		EnumSet<ButtonType> buttonTypeSet = EnumSet.of(ButtonType.EDIT_ALBUM,
			  	 ButtonType.PLAY_ALBUM);
		if (collectionId != null) { // We're in an album view of a collection
			buttonSet.add(ButtonLineParam.DELETE);
			buttonTypeSet.add(ButtonType.DELETE_COLLECTION_ALBUM);
		}
		ButtonLine b = new ButtonLine(buttonSet, buttonTypeSet);
		setEditButtonHandler(b);
		setAddAtEndButtonHandler(b);
		setPlayButtonHandler(b);
		setDeleteButtonHandler(b);
		labelAndButtonsPanel.add(b);
		
		// Artist list
		if (artistId == null) { // This is a view of various artists, should show artists in each album
			for (String id : album.getArtistList()) {
				Controller.INSTANCE.getModel().getArtistCache().addHandler(id, new CacheHandler<String, ArtistInfo>() {
					@Override
					public void onCacheUpdated(String k, ArtistInfo v) {
						if (artistsOfAlbumLabel.getText().isEmpty()) {
							artistsOfAlbumLabel.setText(Constants.I18N.by() + " " + v.getName());
						} else {
							artistsOfAlbumLabel.setText(artistsOfAlbumLabel.getText() + ", " + v.getName());
						}
					}
				});
				Controller.INSTANCE.getModel().getArtistCache().obtain(id);
			}
		} else {
			artistsOfAlbumPanel.setVisible(false);
		}
		
		// Add song list
		songsPanel.add(new ContextualSongList(album.getId(), artistId, collectionId, album.getNumberOfSongs(), inPanel));
		
		// Make image draggable
		albumImagePanel.setProperties(album.getId(), artistId, collectionId, album.getNumberOfSongs());
		Controller.INSTANCE.makeDraggable(albumImagePanel);
	}

	private void setImage() {
		ImageHelper.setImage(albumImage, album.getImageUrl(), Resources.INSTANCE.album());
	}

	private void setClickTarget(String collectionId) {
		final String target = Utils.getTargetHistoryToken(null, album.getId(), collectionId, null);
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
	
	private void setDeleteButtonHandler(ButtonLine b) {
		b.setDeleteButtonHandler(new DeleteButtonHandler() {
			@Override
			public void onDelete() {
				Controller.INSTANCE.removeFromCollection(collectionId, artistId, album.getId(), new DeleteButtonHandler() {
					@Override
					public void onDelete() {
						// When deletion is complete handle the "deletion" of the widget from UI
						AlbumWidget.this.removeFromParent();
					}
				});
			}
		});
	}

}

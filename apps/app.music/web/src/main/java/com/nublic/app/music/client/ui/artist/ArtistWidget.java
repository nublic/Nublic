package com.nublic.app.music.client.ui.artist;

import java.util.EnumSet;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Resources;
import com.nublic.app.music.client.Utils;
import com.nublic.app.music.client.controller.Controller;
import com.nublic.app.music.client.datamodel.AlbumInfo;
import com.nublic.app.music.client.datamodel.ArtistInfo;
import com.nublic.app.music.client.datamodel.handlers.AddAtEndButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.AlbumHandler;
import com.nublic.app.music.client.datamodel.handlers.DeleteButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.EditButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.PlayButtonHandler;
import com.nublic.app.music.client.ui.ButtonLine;
import com.nublic.app.music.client.ui.ButtonLineParam;
import com.nublic.app.music.client.ui.ButtonType;
import com.nublic.util.widgets.ImageHelper;

//GET /artist-art/:artist-id
//* Retrieve the image associated with an artist
//* Return: the raw image data
public class ArtistWidget extends Composite {
	private static ArtistWidgetUiBinder uiBinder = GWT.create(ArtistWidgetUiBinder.class);
	interface ArtistWidgetUiBinder extends UiBinder<Widget, ArtistWidget> {}

	// CSS Styles defined in the .xml file
	interface ArtistStyle extends CssResource {
		String inlineblock();
		String minheight();
		String myh3andahalf();
		String artlimits();
		String handover();
	}

	@UiField ArtistStyle style;
	@UiField ArtistImage artistImage;
	@UiField Hyperlink artistNameLabel;
	@UiField FlowPanel albumsPanel;
	@UiField HorizontalPanel labelAndButtonsPanel;
	
	ArtistInfo artist;
	String collectionId;
	boolean loaded = false;

	public ArtistWidget(ArtistInfo art, String collectionId) {
		initWidget(uiBinder.createAndBindUi(this));

		this.artist = art;
		this.collectionId = collectionId;
		
		artistNameLabel.setText(artist.getName());
//		lazyLoad();
	}

	public void lazyLoad() {
		if (!loaded) {
			loaded = true;
			
			setImage();
			setClickTarget();
			setMyselfAsAlbumHandler();
			
			// Add button line
			EnumSet<ButtonLineParam> buttonSet = EnumSet.of(ButtonLineParam.EDIT,
					 ButtonLineParam.ADD_AT_END,
					 ButtonLineParam.PLAY);
			EnumSet<ButtonType> buttonTypeSet = EnumSet.of(ButtonType.EDIT_ARTIST,
				     ButtonType.PLAY_ARTIST);
			if (collectionId != null) { // We're in an album view of a collection
				buttonSet.add(ButtonLineParam.DELETE);
				buttonTypeSet.add(ButtonType.DELETE_COLLECTION_ARTIST);
			}
			ButtonLine b = new ButtonLine(buttonSet, buttonTypeSet);
			setEditButtonHandler(b);
			setAddAtEndButtonHandler(b);
			setPlayButtonHandler(b);
			setDeleteButtonHandler(b);
			labelAndButtonsPanel.add(b);
		}
	}

	private void setImage() {
		ImageHelper.setImage(artistImage, artist.getImageUrl(), Resources.INSTANCE.artist());
		
		// Drag and drop
		artistImage.setProperties(artist.getId(), collectionId, artist.getNumberOfSongs());
		Controller.INSTANCE.makeDraggable(artistImage);
	}

	private void setClickTarget() {
		final String target = Utils.getTargetHistoryToken(artist.getId(), null, collectionId, null);
		artistNameLabel.setTargetHistoryToken(target);
		artistImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				History.newItem(target);
			}
		});
	}

	// To handle answers to album messages and add album widgets
	private void setMyselfAsAlbumHandler() {
		Controller.INSTANCE.getModel().askForAlbums(artist.getId(), collectionId, new AlbumHandler() {
			@Override
			public void onAlbumChange(List<AlbumInfo> answerList) {
				for (AlbumInfo a : answerList) {
					AlbumInArtist aw = new AlbumInArtist(a, artist.getId(), collectionId);
					aw.getElement().addClassName(style.inlineblock());
					albumsPanel.add(aw);
				}
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
				Controller.INSTANCE.addAtEnd(artist.getId(), null, collectionId);
			}
		});
	}

	private void setPlayButtonHandler(ButtonLine b) {
		b.setPlayButtonHandler(new PlayButtonHandler() {
			@Override
			public void onPlay() {
				Controller.INSTANCE.play(artist.getId(), null, collectionId);
			}
		});
	}
	
	private void setDeleteButtonHandler(ButtonLine b) {
		b.setDeleteButtonHandler(new DeleteButtonHandler() {
			@Override
			public void onDelete() {
				Controller.INSTANCE.removeFromCollection(collectionId, artist.getId(), null, new DeleteButtonHandler() {
					@Override
					public void onDelete() {
						// When deletion is complete handle the "deletion" of the widget from UI
						ArtistWidget.this.removeFromParent();
					}
				});
			}
		});
	}
}

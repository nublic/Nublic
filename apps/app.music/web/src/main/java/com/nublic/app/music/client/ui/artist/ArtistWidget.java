package com.nublic.app.music.client.ui.artist;

import java.util.EnumSet;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.Resources;
import com.nublic.app.music.client.datamodel.AlbumInfo;
import com.nublic.app.music.client.datamodel.ArtistInfo;
import com.nublic.app.music.client.datamodel.Controller;
import com.nublic.app.music.client.datamodel.handlers.AddAtEndButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.AlbumHandler;
import com.nublic.app.music.client.datamodel.handlers.EditButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.PlayButtonHandler;
import com.nublic.app.music.client.ui.ButtonLine;
import com.nublic.app.music.client.ui.ButtonLineParam;

//GET /artist-art/:artist-id
//* Retrieve the image associated with an artist
//* Return: the raw image data
public class ArtistWidget extends Composite {
	private static ArtistWidgetUiBinder uiBinder = GWT.create(ArtistWidgetUiBinder.class);
	interface ArtistWidgetUiBinder extends UiBinder<Widget, ArtistWidget> {}

	// CSS Styles defined in the .xml file
	interface ArtistStyle extends CssResource {
		String inlineblock();
		String padding();
		String rightmargin();
		String leftmargin();
		String minheight();
	}

	@UiField ArtistStyle style;
	@UiField Image artistImage;
	@UiField Hyperlink artistNameLabel;
//	@UiField AbsolutePanel artistPanel;
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
			ButtonLine b = new ButtonLine(EnumSet.of(ButtonLineParam.EDIT,
													 ButtonLineParam.ADD_AT_END,
													 ButtonLineParam.PLAY));
			setEditButtonHandler(b);
			setAddAtEndButtonHandler(b);
			setPlayButtonHandler(b);
			labelAndButtonsPanel.add(b);
		}
	}

	private void setImage() {
		artistImage.addErrorHandler(new ErrorHandler() {
			@Override
			public void onError(ErrorEvent event) {
				artistImage.setResource(Resources.INSTANCE.artist());
			}
		});
//		artistImage.setUrl(URL.encode(imageUrl.toString()));
		artistImage.setUrl(artist.getImageUrl());
	}

	private void setClickTarget() {
		StringBuilder target = new StringBuilder();		
		if (collectionId != null) {
			target.append(Constants.PARAM_COLLECTION);
			target.append("=");
			target.append(collectionId);
			target.append("&");
		}
		target.append(Constants.PARAM_ARTIST);
		target.append("=");
		target.append(artist.getId());
		artistNameLabel.setTargetHistoryToken(target.toString());
	}

	// To handle answers to album messages and add album widgets
	private void setMyselfAsAlbumHandler() {
		Controller.getModel().askForAlbums(artist.getId(), collectionId, new AlbumHandler() {
			@Override
			public void onAlbumChange(List<AlbumInfo> answerList) {
				for (AlbumInfo a : answerList) {
					AlbumInArtist aw = new AlbumInArtist(a, collectionId);
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
				Controller.addAtEnd(artist.getId(), null, collectionId);
			}
		});
	}

	private void setPlayButtonHandler(ButtonLine b) {
		b.setPlayButtonHandler(new PlayButtonHandler() {
			@Override
			public void onPlay() {
				Controller.play(artist.getId(), null, collectionId);
			}
		});
	}
}

package com.nublic.app.music.client.ui.album;

import java.util.EnumSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.Resources;
import com.nublic.app.music.client.datamodel.Album;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.handlers.AddAtEndButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.EditButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.PlayButtonHandler;
import com.nublic.app.music.client.ui.ButtonLine;
import com.nublic.app.music.client.ui.ButtonLineParam;
import com.nublic.app.music.client.ui.song.SongList;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.FlowPanel;

//GET /album-art/:album-id
//* Retrieve the image associated with an album
//* Return: the raw image data

public class AlbumWidget extends Composite {
	private static AlbumWidgetUiBinder uiBinder = GWT.create(AlbumWidgetUiBinder.class);
	interface AlbumWidgetUiBinder extends UiBinder<Widget, AlbumWidget> { }

	@UiField HorizontalPanel labelAndButtonsPanel;
	@UiField Hyperlink albumNameLabel;
	@UiField Image albumImage;
	@UiField FlowPanel songsPanel;
	Album album;
	
	public AlbumWidget(DataModel model, Album a, Widget inPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		album = a;

		setImage();

		albumNameLabel.setText(album.getInfo().getName());
		setClickTarget();
		
		// Add button line
		ButtonLine b = new ButtonLine(EnumSet.of(ButtonLineParam.EDIT,
												 ButtonLineParam.ADD_AT_END,
												 ButtonLineParam.PLAY));
		setEditButtonHandler(b);
		setAddAtEndButtonHandler(b);
		setPlayButtonHandler(b);
		labelAndButtonsPanel.add(b);
		
		// Add song list
		songsPanel.add(new SongList(model, album, inPanel)); // Needs the model to access cache
	}

	private void setImage() {
		// building imageUrl as /album-art/:album-id
		StringBuilder imageUrl = new StringBuilder();
		imageUrl.append(GWT.getHostPageBaseURL());
		imageUrl.append("server/album-art/");
		imageUrl.append(album.getInfo().getId());

		albumImage.addErrorHandler(new ErrorHandler() {
			@Override
			public void onError(ErrorEvent event) {
				albumImage.setResource(Resources.INSTANCE.album());
			}
		});
		
		albumImage.setUrl(URL.encode(imageUrl.toString()));
	}

	private void setClickTarget() {
		StringBuilder target = new StringBuilder();		
		if (album.getInCollection() != null) {
			target.append(Constants.PARAM_COLLECTION);
			target.append("=");
			target.append(album.getInCollection());
			target.append("&");
		}
		target.append(Constants.PARAM_ALBUM);
		target.append("=");
		target.append(album.getInfo().getId());
		albumNameLabel.setTargetHistoryToken(target.toString());
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
				// TODO: addAtEnd
			}
		});
	}

	private void setPlayButtonHandler(ButtonLine b) {
		b.setPlayButtonHandler(new PlayButtonHandler() {
			@Override
			public void onPlay() {
				// TODO: play
			}
		});
	}

}

package com.nublic.app.music.client.ui.artist;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.datamodel.Album;
import com.nublic.app.music.client.datamodel.Artist;
import com.nublic.app.music.client.datamodel.handlers.AlbumsChangeHandler;

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
		String nobackground();
		String leftmargin();
		String rightmargin();
		String semitransparent();
		String transparent();
		String alignmiddle();
	}

	@UiField Image artistImage;
	@UiField Hyperlink artistNameLabel;
	@UiField PushButton editButton;
	@UiField PushButton addAtEndButton;
	@UiField PushButton playButton;
	@UiField AbsolutePanel artistPanel;
	@UiField FlowPanel albumsPanel;
	@UiField ArtistStyle style;
	Artist artist;

	public ArtistWidget(Artist art) {
		initWidget(uiBinder.createAndBindUi(this));

		this.artist = art;

		// building imageUrl as /artist-art/:artist-id
		StringBuilder imageUrl = new StringBuilder();
		imageUrl.append(GWT.getHostPageBaseURL());
		imageUrl.append("server/artist-art/");
		imageUrl.append(art.getId());
		
		artistImage.setUrl(URL.encode(imageUrl.toString()));
		artistNameLabel.setText(art.getName());
		setClickTarget();
		
		setMyselfAsAlbumHandler();
		addMouseOverHandler();
	}

	private void setClickTarget() {
		StringBuilder target = new StringBuilder();		
		if (artist.getInCollection() != null) {
			target.append(Constants.PARAM_COLLECTION);
			target.append("=");
			target.append(artist.getInCollection());
			target.append("&");
		}
		target.append(Constants.PARAM_ARTIST);
		target.append("=");
		target.append(artist.getId());
		artistNameLabel.setTargetHistoryToken(target.toString());
	}

	// To handle answers to album messages and add album widgets
	private void setMyselfAsAlbumHandler() {
		artist.setAlbumsHandler(new AlbumsChangeHandler() {
			@Override
			public void onAlbumsChange() {
				List<Album> albumList = artist.getAlbumList();
				for (Album a : albumList) {
					AlbumInArtist aw = new AlbumInArtist(a, artist.getInCollection());
					aw.getElement().addClassName(style.inlineblock());
					albumsPanel.add(aw);
				}
			}
		});
		artist.askForAlbums();
	}
	
	
	// For handling mouse in and out efects over push buttons
	private void addMouseOverHandler() {
		TransparentMouseEventHandler mouseHandler = new TransparentMouseEventHandler();
		artistPanel.addDomHandler(mouseHandler, MouseOverEvent.getType());
		artistPanel.addDomHandler(mouseHandler, MouseOutEvent.getType());
		
		new SemitransparentMouseEventHandler(editButton);
		new SemitransparentMouseEventHandler(addAtEndButton);
		new SemitransparentMouseEventHandler(playButton);
//		editButton.addDomHandler(mouseHandler2, MouseOverEvent.getType());
//		editButton.addDomHandler(mouseHandler2, MouseOutEvent.getType());
//		addAtEndButton.addDomHandler(mouseHandler2, MouseOverEvent.getType());
//		addAtEndButton.addDomHandler(mouseHandler2, MouseOutEvent.getType());
//		playButton.addDomHandler(mouseHandler2, MouseOverEvent.getType());
//		playButton.addDomHandler(mouseHandler2, MouseOutEvent.getType());
	}
	
	public class TransparentMouseEventHandler implements MouseOverHandler, MouseOutHandler {
		public void onMouseOver(final MouseOverEvent moe) {
			editButton.getElement().removeClassName(style.transparent());
			addAtEndButton.getElement().removeClassName(style.transparent());
			playButton.getElement().removeClassName(style.transparent());
		}
		public void onMouseOut(final MouseOutEvent moe) {
			editButton.getElement().addClassName(style.transparent());
			addAtEndButton.getElement().addClassName(style.transparent());
			playButton.getElement().addClassName(style.transparent());
		}
	}
	
	public class SemitransparentMouseEventHandler implements MouseOverHandler, MouseOutHandler {
		PushButton internalButton;
		
		public SemitransparentMouseEventHandler(PushButton b) {
			internalButton = b;
			internalButton.addDomHandler(this, MouseOverEvent.getType());
			internalButton.addDomHandler(this, MouseOutEvent.getType());
		}

		public void onMouseOver(final MouseOverEvent moe) {
			internalButton.getElement().removeClassName(style.semitransparent());
		}
		public void onMouseOut(final MouseOutEvent moe) {
			internalButton.getElement().addClassName(style.semitransparent());
		}
	}

}

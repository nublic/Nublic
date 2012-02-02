package com.nublic.app.music.client.ui.artist;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.datamodel.Album;
import com.nublic.app.music.client.datamodel.Artist;
import com.nublic.app.music.client.datamodel.handlers.AlbumsChangeHandler;
import com.nublic.app.music.client.ui.ButtonLine;

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
	}

	@UiField Image artistImage;
	@UiField Hyperlink artistNameLabel;
	@UiField AbsolutePanel artistPanel;
	@UiField FlowPanel albumsPanel;
	@UiField ArtistStyle style;
	@UiField HorizontalPanel labelAndButtonsPanel;
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
		labelAndButtonsPanel.add(new ButtonLine(true, true, true, labelAndButtonsPanel));
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
}

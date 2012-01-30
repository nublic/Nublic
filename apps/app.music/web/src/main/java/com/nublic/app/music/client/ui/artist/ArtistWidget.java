package com.nublic.app.music.client.ui.artist;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.nublic.app.music.client.datamodel.Album;
import com.nublic.app.music.client.datamodel.Artist;
import com.nublic.app.music.client.datamodel.handlers.AlbumsChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;

//GET /artist-art/:artist-id
//* Retrieve the image associated with an artist
//* Return: the raw image data

public class ArtistWidget extends Composite {
	private static ArtistWidgetUiBinder uiBinder = GWT.create(ArtistWidgetUiBinder.class);
	interface ArtistWidgetUiBinder extends UiBinder<Widget, ArtistWidget> {}

	@UiField Image artistImage;
	@UiField Label artistNameLabel;
	@UiField FlowPanel albumsPanel;

	public ArtistWidget(Artist art) {
		initWidget(uiBinder.createAndBindUi(this));
		
		// building imageUrl as /artist-art/:artist-id
		StringBuilder imageUrl = new StringBuilder();
		imageUrl.append(GWT.getHostPageBaseURL());
		imageUrl.append("server/artist-art/");
		imageUrl.append(art.getId());
		
		artistImage.setUrl(URL.encode(imageUrl.toString()));
		artistNameLabel.setText(art.getName());
		
		setMyselfAsAlbumHandler(art);
	}

	private void setMyselfAsAlbumHandler(final Artist artist) {
		artist.setAlbumsHandler(new AlbumsChangeHandler() {
			@Override
			public void onAlbumsChange() {
				List<Album> albumList = artist.getAlbumList();
				for (Album a : albumList) {
					AlbumInArtist aw = new AlbumInArtist(a);
					albumsPanel.add(aw);
				}
			}
		});
		artist.askForAlbums();
	}

}

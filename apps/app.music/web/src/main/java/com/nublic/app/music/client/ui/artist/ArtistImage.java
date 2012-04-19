package com.nublic.app.music.client.ui.artist;

import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Image;
import com.nublic.app.music.client.ui.dnd.HasArtistInfo;

public class ArtistImage extends Image implements HasArtistInfo, HasMouseDownHandlers {
	String artistId;
	String collectionId;
	int numberOfSongs;

	public void setProperties(String artistId, String collectionId, int numberOfSongs) {
		this.artistId = artistId;
		this.collectionId = collectionId;
		this.numberOfSongs = numberOfSongs;
	}

	@Override
	public String getArtistId() {
		return artistId;
	}

	@Override
	public String getCollectionId() {
		return collectionId;
	}

	@Override
	public int getNumberOfSongs() {
		return numberOfSongs;
	}
	
	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}
}

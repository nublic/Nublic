package com.nublic.app.music.client.datamodel;

import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.ParamsHashMap;
import com.nublic.app.music.client.datamodel.handlers.AlbumHandler;
import com.nublic.app.music.client.ui.MainUi;

public class Controller {

	DataModel model;
	MainUi ui;
	
	public Controller(DataModel model, MainUi ui) {
		this.model = model;
		this.ui = ui;
	}

	// When URL changes this method is called
	public void changeState(ParamsHashMap hmap) {
		String collection = hmap.get(Constants.PARAM_COLLECTION);
		String playlist = hmap.get(Constants.PARAM_PLAYLIST);
		String artist = hmap.get(Constants.PARAM_ARTIST);
		String album = hmap.get(Constants.PARAM_ALBUM);

		if (collection != null) {
			if (artist != null) {
				model.askForAlbums(artist, collection, new MyAlbumHandler());
			} else if (album != null) {
				model.askForSongs(album, collection);
			} else {
				model.askForArtists(collection);
			}
		} else if (playlist != null) {
			model.askForPlaylistSongs(playlist);
		} else {
			// All music
			if (artist != null) {
				model.askForAlbums(artist);
			} else if (album != null) {
				model.askForSongs(album);
			} else {
				model.askForArtists();
			}
		}
	}
	
	class MyAlbumHandler implements AlbumHandler {
		@Override
		public void onAlbumChange() {
			
		}
	}


}

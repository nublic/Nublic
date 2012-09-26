package com.nublic.app.init.client.controller;

import com.nublic.app.init.client.model.InitModel;
import com.nublic.app.init.client.ui.MainUi;
import com.nublic.util.messages.ParamsHashMap;

public class URLController {
	InitModel model;
	MainUi ui;

	protected URLController(InitModel model, MainUi ui) {
		this.ui = ui;
		this.model = model;
	}

	// +++++ Handle history state change ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// When URL changes this method is called
	public void changeState(ParamsHashMap hmap) {
//		String collection = hmap.get(Constants.PARAM_COLLECTION);
//		String playlist = hmap.get(Constants.PARAM_PLAYLIST);
//		String artist = hmap.get(Constants.PARAM_ARTIST);
//		String album = hmap.get(Constants.PARAM_ALBUM);
//		String view = hmap.get(Constants.PARAM_VIEW);
//		ViewKind newViewKind = ViewKind.parse(view);
//
//		if (newViewKind != null) {
//			viewKind = newViewKind;
//		}
//
//		if (playlist != null) {
//			model.askForPlaylistSongs(playlist, new MyPlaylistHandler(playlist), true);
//		} else {
//			if (viewKind == ViewKind.SONGS || album != null) {
//				model.askForSongs(album, artist, collection, new MySongHandler(album, artist, collection), true);
//			} else if (viewKind == ViewKind.ALBUMS || artist != null) {
//				model.askForAlbums(artist, collection, new MyAlbumHandler(artist, collection), true);
//			} else {
//				model.askForArtists(collection, new MyArtistHandler(collection), true);
//			}
//		}
	}
	
	// Song
//	class MySongHandler implements SongHandler {
//		String albumId;
//		String collectionId;
//		String artistId;
//		public MySongHandler(String albumId, String artistId, String collection) {
//			this.albumId = albumId;
//			this.collectionId = collection;
//			this.artistId = artistId;
//		}
//		@Override
//		public void onSongsChange(int total, int from, int to, List<SongInfo> answerList) {
//			ui.showSongList(total, from, to, answerList, albumId, artistId, collectionId);
//		}
//	}
}

package com.nublic.app.music.client.datamodel;

import java.util.List;

import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.ParamsHashMap;
import com.nublic.app.music.client.datamodel.handlers.AlbumHandler;
import com.nublic.app.music.client.datamodel.handlers.ArtistHandler;
import com.nublic.app.music.client.datamodel.handlers.SongHandler;
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
				model.askForAlbums(artist, collection, new MyAlbumHandler(artist, collection), true);
			} else if (album != null) {
				model.askForSongs(album, collection, new MySongHandler(album, collection), true);
			} else {
				model.askForArtists(collection, new MyArtistHandler(collection), true);
			}
		} else if (playlist != null) {
//			model.askForPlaylistSongs(playlist);
		} else {
			// All music
			if (artist != null) {
				model.askForAlbums(artist, null, new MyAlbumHandler(artist, null), true);
			} else if (album != null) {
				model.askForSongs(album, null, new MySongHandler(album, null), true);
			} else {
				model.askForArtists(null, new MyArtistHandler(collection), true);
			}
		}
	}
	
	class MySongHandler implements SongHandler {
		String albumId;
		String collectionId;	
		
		public MySongHandler(String albumId, String collection) {
			this.albumId = albumId;
			this.collectionId = collection;
		}
		
		@Override
		public void onSongsChange(int total, int from, int to, List<SongInfo> answerList) {
			ui.showSongList(total, from, to, answerList, albumId, collectionId);
		}
	}
	
	class MyAlbumHandler implements AlbumHandler {
		String artistId;
		String collectionId;	
		
		public MyAlbumHandler(String artist, String collection) {
			artistId = artist;
			collectionId = collection;
		}
		
		@Override
		public void onAlbumChange(List<AlbumInfo> answerList) {
			ui.showAlbumList(answerList, artistId, collectionId);
		}
	}
	
	class MyArtistHandler implements ArtistHandler {
		String collectionId;

		public MyArtistHandler(String collection) {
			collectionId = collection;
		}

		@Override
		public void onArtistChange(List<ArtistInfo> answerList) {
			ui.showArtistList(answerList, collectionId);
		}
	}


}

package com.nublic.app.music.client.controller;

import java.util.List;

import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.ParamsHashMap;

import com.nublic.app.music.client.datamodel.AlbumInfo;
import com.nublic.app.music.client.datamodel.ArtistInfo;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.app.music.client.datamodel.handlers.AlbumHandler;
import com.nublic.app.music.client.datamodel.handlers.ArtistHandler;
import com.nublic.app.music.client.datamodel.handlers.SongHandler;
import com.nublic.app.music.client.ui.MainUi;

public class URLController {
	DataModel model;
	MainUi ui;
	ViewKind viewKind = ViewKind.ARTISTS;

	protected URLController(DataModel model, MainUi ui) {
		this.ui = ui;
		this.model = model;
	}

	// +++++ Handle history state change ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// When URL changes this method is called
	public void changeState(ParamsHashMap hmap) {
		String collection = hmap.get(Constants.PARAM_COLLECTION);
		String playlist = hmap.get(Constants.PARAM_PLAYLIST);
		String artist = hmap.get(Constants.PARAM_ARTIST);
		String album = hmap.get(Constants.PARAM_ALBUM);
		String view = hmap.get(Constants.PARAM_VIEW);
		ViewKind newViewKind = ViewKind.parse(view);
		
		if (newViewKind != null) {
			viewKind = newViewKind;
		}

		if (playlist != null) {
			model.askForPlaylistSongs(playlist, new MyPlaylistHandler(playlist), true);
		} else {
//			if (album != null) {
//				model.askForSongs(album, artist, collection, new MySongHandler(album, collection), true);
//			} else if (artist != null) {
//				model.askForAlbums(artist, collection, new MyAlbumHandler(artist, collection), true);
//			} else {
//				model.askForArtists(collection, new MyArtistHandler(collection), true);
//			}
			switch (viewKind) {
			case ARTISTS:
				model.askForArtists(collection, new MyArtistHandler(collection), true);
				break;
			case ALBUMS:
				model.askForAlbums(artist, collection, new MyAlbumHandler(artist, collection), true);
				break;
			case SONGS:
				model.askForSongs(album, artist, collection, new MySongHandler(album, collection), true);
				break;
			}
		}
	}
	
	// Song
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
	
	// Album
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
	
	// Artist
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

	// Playlist
	class MyPlaylistHandler implements SongHandler {
		String playlistId;
		public MyPlaylistHandler(String playlistId) {
			this.playlistId = playlistId;
		}
		@Override
		public void onSongsChange(int total, int from, int to, List<SongInfo> answerList) {
			ui.showPlaylist(total, from, to, answerList, playlistId);
		}
	}
}

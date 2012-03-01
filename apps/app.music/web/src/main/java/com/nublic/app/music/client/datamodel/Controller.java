package com.nublic.app.music.client.datamodel;

import java.util.List;

import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.ParamsHashMap;
import com.nublic.app.music.client.datamodel.handlers.AlbumHandler;
import com.nublic.app.music.client.datamodel.handlers.ArtistHandler;
import com.nublic.app.music.client.datamodel.handlers.PlaylistHandler;
import com.nublic.app.music.client.datamodel.handlers.SongHandler;
import com.nublic.app.music.client.ui.MainUi;
import com.nublic.app.music.client.ui.player.NublicPlayer;
import com.nublic.util.cache.Cache;

public class Controller {
	static NublicPlayer player;
	static Cache<String, AlbumInfo> albumCache;
	static Cache<String, ArtistInfo> artistCache;
	DataModel model;
	MainUi ui;
	
	public Controller(DataModel model, MainUi ui) {
		this.model = model;
		this.ui = ui;
		
		Controller.setPlayer(ui.getPlayer());
		Controller.setAlbumCache(model.getAlbumCache());
		Controller.setArtistCache(model.getArtistCache());
	}
	
	// Getters and setters of singletones
	public static NublicPlayer getPlayer() { return player; }
	public static void setPlayer(NublicPlayer p) { player = p; }
	public static Cache<String, AlbumInfo> getAlbumCache() { return albumCache; }
	public static void setAlbumCache(Cache<String, AlbumInfo> albumCache) {	Controller.albumCache = albumCache; }
	public static Cache<String, ArtistInfo> getArtistCache() { return artistCache; }
	public static void setArtistCache(Cache<String, ArtistInfo> artistCache) { Controller.artistCache = artistCache; }

	// When URL changes this method is called
	public void changeState(ParamsHashMap hmap) {
		String collection = hmap.get(Constants.PARAM_COLLECTION);
		String playlist = hmap.get(Constants.PARAM_PLAYLIST);
		String artist = hmap.get(Constants.PARAM_ARTIST);
		String album = hmap.get(Constants.PARAM_ALBUM);

		if (playlist != null) {
			model.askForPlaylistSongs(playlist, new MyPlaylistHandler(playlist), true);
		} else {
			if (album != null) {
				model.askForSongs(album, artist, collection, new MySongHandler(album, collection), true);
			} else if (artist != null) {
				model.askForAlbums(artist, collection, new MyAlbumHandler(artist, collection), true);
			} else {
				model.askForArtists(collection, new MyArtistHandler(collection), true);
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
	class MyPlaylistHandler implements PlaylistHandler {
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

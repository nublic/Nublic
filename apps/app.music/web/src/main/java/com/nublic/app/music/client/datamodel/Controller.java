package com.nublic.app.music.client.datamodel;

import java.util.List;

import com.bramosystems.oss.player.core.event.client.PlayStateEvent;
import com.bramosystems.oss.player.core.event.client.PlayStateHandler;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.ParamsHashMap;
import com.nublic.app.music.client.datamodel.handlers.AlbumHandler;
import com.nublic.app.music.client.datamodel.handlers.ArtistHandler;
import com.nublic.app.music.client.datamodel.handlers.SongHandler;
import com.nublic.app.music.client.ui.MainUi;
import com.nublic.app.music.client.ui.player.NublicPlayer;

public class Controller {
	static NublicPlayer player;
	static DataModel model;
	MainUi ui;
	
	// Depending on what is being played
	static String playingPlaylistId = Constants.CURRENT_PLAYLIST_ID;
	
	public Controller(DataModel model, MainUi ui) {
		this.ui = ui;
		
		Controller.setPlayer(ui.getPlayer());
		Controller.setModel(model);
		
		addPlayHandler();
	}

	// Getters and setters of singletones
	public static NublicPlayer getPlayer() { return player; }
	public static void setPlayer(NublicPlayer p) { player = p; }
	public static String getPlayingPlaylistId() { return playingPlaylistId; }
	public static void setPlayingPlaylistId(String playingPlaylistId) { Controller.playingPlaylistId = playingPlaylistId; }
	public static DataModel getModel() { return model; }
	public static void setModel(DataModel model) { Controller.model = model; }
	
	// Utils to music reproduction
	public static void setPlayingList(String playlistId) {
		if (!playlistId.equals(playingPlaylistId)) {
			player.clearNublicPlaylist();
			playingPlaylistId = playlistId;
			// TODO: load the new playlist
		}
	}

	public static void play(String artistId, String albumId, String collectionId) {
		model.askForSongs(0, 32000, albumId, artistId, collectionId, new SongHandler() {
			@Override
			public void onSongsChange(int total, int from, int to, List<SongInfo> answerList) {
				setPlayingList(Constants.CURRENT_PLAYLIST_ID);
				model.clearCurrentPlaylist();
				model.addToCurrentPlaylist(answerList);
				player.clearNublicPlaylist();
				player.addSongsToPlaylist(answerList);
				player.nublicPlay();
			}
		}, false);
	}
	
	public static void addAtEnd(String artistId, String albumId, String collectionId) {
		model.askForSongs(0, 32000, albumId, artistId, collectionId, new SongHandler() {
			@Override
			public void onSongsChange(int total, int from, int to, List<SongInfo> answerList) {
				model.addToCurrentPlaylist(answerList);
				player.addSongsToPlaylist(answerList);
			}
		}, false);
	}

	public static void addAtEndOfCurrentPlaylist(SongInfo s) {
		model.addToCurrentPlaylist(s);
		player.addSongToPlaylist(s);
	}
	
	// Plays a song from a collection
	public static void play(SongInfo s) {
		setPlayingList(Constants.CURRENT_PLAYLIST_ID);
		addAtEndOfCurrentPlaylist(s);
		player.playSong(player.getNublicPlaylistSize() -1);
	}
	
	// Plays a song from a playlist
	public static void play(int row, String playlistId) {
		setPlayingList(playlistId);
		player.playSong(row);
	}

	private void addPlayHandler() {
		if (ui.getPlayer() != null) {
			ui.getPlayer().addPlayStateHandler(new PlayStateHandler() {
				@Override
				public void onPlayStateChanged(PlayStateEvent event) {
					switch (event.getPlayState()) {
					case Paused:
						ui.setPaused(Controller.getPlayingPlaylistId());
	            		break;
	            	case Started:
						ui.setPlaying(Controller.getPlayingPlaylistId());
	            		break;
	            	case Stopped:
						ui.setPlaying(null);
	            		break;
	            	case Finished:
						ui.setPlaying(null);
	            		break;
					}
				}
			});
		}
	}
 
	public static void saveCurrentPlaylist() {
		// TODO: This should pop up a panel to ask the name of the new playlist
		// Create the playlist
		// and then put "current playlist" to it 
	}
	
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

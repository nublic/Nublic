package com.nublic.app.music.client.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.datamodel.handlers.AlbumHandler;
import com.nublic.app.music.client.datamodel.handlers.ArtistHandler;
import com.nublic.app.music.client.datamodel.handlers.DeleteButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.MoveSongHandler;
import com.nublic.app.music.client.datamodel.handlers.PlaylistsChangeHandler;
import com.nublic.app.music.client.datamodel.handlers.SavePlaylistSuccessHandler;
import com.nublic.app.music.client.datamodel.handlers.SongHandler;
import com.nublic.app.music.client.datamodel.handlers.TagsChangeHandler;
import com.nublic.app.music.client.datamodel.handlers.PlaylistsChangeHandler.PlaylistsChangeEvent;
import com.nublic.app.music.client.datamodel.handlers.TagsChangeHandler.TagsChangeEvent;
import com.nublic.app.music.client.datamodel.messages.AddPlaylistMessage;
import com.nublic.app.music.client.datamodel.messages.AddTagMessage;
import com.nublic.app.music.client.datamodel.messages.AddToCollectionMessage;
import com.nublic.app.music.client.datamodel.messages.AddToPlaylistMessage;
import com.nublic.app.music.client.datamodel.messages.AlbumMessage;
import com.nublic.app.music.client.datamodel.messages.ArtistMessage;
import com.nublic.app.music.client.datamodel.messages.DeletePlaylistMessage;
import com.nublic.app.music.client.datamodel.messages.DeletePlaylistSongMessage;
import com.nublic.app.music.client.datamodel.messages.DeleteTagMessage;
import com.nublic.app.music.client.datamodel.messages.MovePlaylistSongMessage;
import com.nublic.app.music.client.datamodel.messages.PlaylistContentMessage;
import com.nublic.app.music.client.datamodel.messages.PlaylistsMessage;
import com.nublic.app.music.client.datamodel.messages.SavePlaylistMessage;
import com.nublic.app.music.client.datamodel.messages.SongMessage;
import com.nublic.app.music.client.datamodel.messages.TagsMessage;
import com.nublic.util.cache.Cache;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.SequenceHelper;

public class DataModel {
	// Independent things
	List<SongInfo> currentPlaylist = new ArrayList<SongInfo>();

	// Handlers
	List<TagsChangeHandler> tagsHandlers = new ArrayList<TagsChangeHandler>();
	List<PlaylistsChangeHandler> playlistsHandlers = new ArrayList<PlaylistsChangeHandler>();

	// Sending messages
	int currentScreen = 0;
	
	// Caches to archive albums and artists
	Cache<String, AlbumInfo> albumCache;
	Cache<String, ArtistInfo> artistCache;
	HashMap<String, Tag> tagCache = new HashMap<String, Tag>();
	HashMap<String, Playlist> playlistCache = new HashMap<String, Playlist>();
	
	// Playlist Delete Locker
	boolean areWeDeleting = false;
	
	public DataModel() {
		// Initialize variables
		initAlbumCache();
		initAristCache();
				
		sendInitialMessages();
	}
	
	private void initAlbumCache() {
		albumCache = new Cache<String, AlbumInfo>() {
			@Override
			public String getURL(String albumId) {
				// GET /album-info/:album-id
				return URL.encode(GWT.getHostPageBaseURL() + "server/album-info/" + albumId);
			}
			@Override
			public AlbumInfo getValue(Response r) {
				if (r.getStatusCode() == Response.SC_OK) {
					return AlbumMessage.parseAlbumInfo(r);
				} else {
					ErrorPopup.showError("Could not access server to refresh album cache");
					return null;
				}
			}
		};
	}

	private void initAristCache() {
		artistCache = new Cache<String, ArtistInfo>() {
			@Override
			public String getURL(String artistId) {
				// GET /artist-info/:artist-id
				return URL.encode(GWT.getHostPageBaseURL() + "server/artist-info/" + artistId);
			}
			@Override
			public ArtistInfo getValue(Response r) {
				if (r.getStatusCode() == Response.SC_OK) {
					return ArtistMessage.parseArtistInfo(r);
				} else {
					ErrorPopup.showError("Could not access server to refresh artist cache");
					return null;
				}
			}
		};
	}
	
	private void sendInitialMessages() {
		TagsMessage tm = new TagsMessage();
		SequenceHelper.sendJustOne(tm, RequestBuilder.GET);
		
		PlaylistsMessage pm = new PlaylistsMessage();
		SequenceHelper.sendJustOne(pm, RequestBuilder.GET);
	}
	
	// Cache
	public Cache<String, AlbumInfo> getAlbumCache() { return albumCache; }
	public Cache<String, ArtistInfo> getArtistCache() { return artistCache; }
	public HashMap<String, Tag> getTagCache() { return tagCache; }
	public HashMap<String, Playlist> getPlaylistCache() { return playlistCache; }

	// Tags
	public void addTagsChangeHandler(TagsChangeHandler h) { tagsHandlers.add(h); }
	public void fireTagsHandlers(TagsChangeEvent event) {
		for (TagsChangeHandler h : tagsHandlers) {
			h.onTagsChange(event);
		}
	}
	
	// Playlists
	public void addPlaylistsChangeHandler(PlaylistsChangeHandler h) { playlistsHandlers.add(h);	}
	public void firePlaylistsHandlers(PlaylistsChangeEvent event) {
		for (PlaylistsChangeHandler h : playlistsHandlers) {
			h.onPlaylistsChange(event);
		}
	}
	
	// methods to make requests to server
	// Songs
	public void askForSongs(String album, String artist, String collection, SongHandler sh) {
		askForSongs(0, Constants.NEXT_SONGS_TO_ASK, album, artist, collection, sh, false);
	}
	public void askForSongs(String album, String artist, String collection, SongHandler sh, boolean newScreen) {
		askForSongs(0, Constants.NEXT_SONGS_TO_ASK, album, artist, collection, sh, false);
	}
	public void askForSongs(int from, int to, String album, String artist, String collection, SongHandler sh) {
		askForSongs(from, to, album, artist, collection, sh, false);
	}
	public void askForSongs(int from, int to, String album, String artist, String collection, SongHandler sh, boolean newScreen) {
		if (newScreen) {
			increaseCurrentScreen();
		}
		SongMessage am = new SongMessage(from, to, album, artist, collection, sh, currentScreen, this);
		SequenceHelper.sendJustOne(am, RequestBuilder.GET);
	}
	
	// Playlists songs
	public void askForPlaylistSongs(String playlist, SongHandler ph) {
		askForPlaylistSongs(0, Constants.NEXT_SONGS_TO_ASK, playlist, ph, false);
	}
	public void askForPlaylistSongs(String playlist, SongHandler ph, boolean newScreen) {
		askForPlaylistSongs(0, Constants.NEXT_SONGS_TO_ASK, playlist, ph, newScreen);
	}
	public void askForPlaylistSongs(int from, int to, String playlist, SongHandler ph) {
		askForPlaylistSongs(from, to, playlist, ph, false);
	}
	public void askForPlaylistSongs(int from, int to, String playlist, SongHandler ph, boolean newScreen) {
		if (newScreen) {
			increaseCurrentScreen();
		}
		if (playlist.equals(Constants.CURRENT_PLAYLIST_ID)) {
			int realTo = to;
			if (to >= currentPlaylist.size()) {
				realTo = currentPlaylist.size() -1;
			}
			if (currentPlaylist.size() == 0) {
				ph.onSongsChange(0, 0, 0, currentPlaylist);
			} else if (from < currentPlaylist.size()) {
				ph.onSongsChange(currentPlaylist.size(), from, realTo, currentPlaylist.subList(from, realTo + 1));
			}
		} else {
			PlaylistContentMessage pm = new PlaylistContentMessage(from, to, playlist, ph, currentScreen, this);
			SequenceHelper.sendJustOne(pm, RequestBuilder.GET);
		}
	}
	
	// Albums
	public void askForAlbums(String artist, String collection, AlbumHandler ah) {
		askForAlbums(artist, collection, ah, false);
	}
	public void askForAlbums(String artist, String collection, AlbumHandler ah, boolean newScreen) {
		if (newScreen) {
			increaseCurrentScreen();
		}
		AlbumMessage am = new AlbumMessage(artist, collection, ah, currentScreen, this);
		SequenceHelper.sendJustOne(am, RequestBuilder.GET);
	}
	
	// Artists
	public void askForArtists(String collection, ArtistHandler ah) {
		askForArtists(collection, ah, false);
	}
	public void askForArtists(String collection, ArtistHandler ah, boolean newScreen) {
		if (newScreen) {
			increaseCurrentScreen();
		}
		ArtistMessage am = new ArtistMessage(collection, ah, currentScreen, this);
		SequenceHelper.sendJustOne(am, RequestBuilder.GET);
	}

	// Control current screen
	public int getCurrentScreen() { return currentScreen; }
	private void increaseCurrentScreen() {
		currentScreen++;
	}

	// methods to change the data in server and where proceeds
	public void putNewTag(String name) {
		AddTagMessage atm = new AddTagMessage(name);
		SequenceHelper.sendJustOne(atm, RequestBuilder.PUT);
	}

	public void putNewPlaylist(String name) {
		AddPlaylistMessage apm = new AddPlaylistMessage(name);
		SequenceHelper.sendJustOne(apm, RequestBuilder.PUT);
	}
	
	public void saveCurrentPlaylist(String name, SavePlaylistSuccessHandler spsh) {
		SavePlaylistMessage spm = new SavePlaylistMessage(name, currentPlaylist, spsh);
		SequenceHelper.sendJustOne(spm, RequestBuilder.PUT);
	}
	
	public void deleteTag(String tagId) {
		DeleteTagMessage dtm = new DeleteTagMessage(tagId);
		SequenceHelper.sendJustOne(dtm, RequestBuilder.DELETE);
	}
	
	public void deletePlaylist(String id) {
		DeletePlaylistMessage dpm = new DeletePlaylistMessage(id);
		SequenceHelper.sendJustOne(dpm, RequestBuilder.DELETE);
	}
	
	public void addToCollection(String collectionId, SongInfo song) {
		AddToCollectionMessage atc = new AddToCollectionMessage(collectionId, song);
		SequenceHelper.sendJustOne(atc, RequestBuilder.PUT);
	}
	
	// current playlist manage methods
	public void addToCurrentPlaylist(SongInfo s) {
		currentPlaylist.add(s);
	}
	
	public void addToPlaylist(String playlistId, SongInfo s) {
		if (playlistId.equals(Constants.CURRENT_PLAYLIST_ID)) {
			addToCurrentPlaylist(s);
		} else {
			AddToPlaylistMessage atpm = new AddToPlaylistMessage(playlistId, s);
			SequenceHelper.sendJustOne(atpm, RequestBuilder.PUT);
		}
	}

	public void addToPlaylist(String playlistId, List<SongInfo> songList) {
		if (playlistId.equals(Constants.CURRENT_PLAYLIST_ID)) {
			for (SongInfo s : songList) {
				addToCurrentPlaylist(s);
			}
		} else {
			AddToPlaylistMessage atpm = new AddToPlaylistMessage(playlistId, songList);
			SequenceHelper.sendJustOne(atpm, RequestBuilder.PUT);
		}
	}
	
	public void clearCurrentPlaylist() {
		currentPlaylist.clear();
	}
	
	public void moveSongInPlaylist(String playlistId, int from, int to, MoveSongHandler msh) {
		if (playlistId.equals(Constants.CURRENT_PLAYLIST_ID)) {
			SongInfo s = currentPlaylist.get(from);
			currentPlaylist.add(to, s);
			currentPlaylist.remove(from > to ? from + 1 : from);
			msh.onSongMoved(playlistId, from, to);
		} else {
			MovePlaylistSongMessage mpsm = new MovePlaylistSongMessage(playlistId, from, to, msh);
			SequenceHelper.sendJustOne(mpsm, RequestBuilder.POST);
		}
	}
	
	public void updateMoveInCache(String id, int from, int to) {
		// TODO: updateMoveInCache (we're not implementing cache yet)
	}

	public synchronized void removeFromPlaylist(String playlistId, int row, DeleteButtonHandler dbh) {
		if (playlistId.equals(Constants.CURRENT_PLAYLIST_ID)) {
			currentPlaylist.remove(row);
			dbh.onDelete();
		} else {
			if (!areWeDeleting) {
				areWeDeleting = true;
				DeletePlaylistSongMessage dpsm = new DeletePlaylistSongMessage(playlistId, row, dbh);
				SequenceHelper.sendJustOne(dpsm, RequestBuilder.DELETE);
			}
		}
	}
	
	public synchronized void setDeleting(boolean b) {
		areWeDeleting = b;
	}


}

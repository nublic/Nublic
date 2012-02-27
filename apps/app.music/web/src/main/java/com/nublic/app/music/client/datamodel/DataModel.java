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
import com.nublic.app.music.client.datamodel.handlers.PlaylistHandler;
import com.nublic.app.music.client.datamodel.handlers.PlaylistsChangeHandler;
import com.nublic.app.music.client.datamodel.handlers.SongHandler;
import com.nublic.app.music.client.datamodel.handlers.TagsChangeHandler;
import com.nublic.app.music.client.datamodel.messages.AddPlaylistMessage;
import com.nublic.app.music.client.datamodel.messages.AddTagMessage;
import com.nublic.app.music.client.datamodel.messages.AlbumMessage;
import com.nublic.app.music.client.datamodel.messages.ArtistMessage;
import com.nublic.app.music.client.datamodel.messages.DeleteTagMessage;
import com.nublic.app.music.client.datamodel.messages.PlaylistContentMessage;
import com.nublic.app.music.client.datamodel.messages.PlaylistsMessage;
import com.nublic.app.music.client.datamodel.messages.SongMessage;
import com.nublic.app.music.client.datamodel.messages.TagsMessage;
import com.nublic.util.cache.Cache;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.SequenceHelper;

public class DataModel {
	// Independent things
	List<Tag> tagList = new ArrayList<Tag>();
	List<Playlist> playlistList = new ArrayList<Playlist>();
//	Playlist currentList = new Playlist(Constants.CURRENT_PLAYLIST_ID, Constants.CURRENT_PLAYLIST_NAME);
	HashMap<String, Tag> tagIndex = new HashMap<String, Tag>();
	HashMap<String, Playlist> playlistIndex = new HashMap<String, Playlist>();

	// Depending on what is being played
	List<SongInfo> currentPlaylist = new ArrayList<SongInfo>();
//	Playlist currentPlayingList;
//	int currentSongInPlaylist;
	
	// Depending on what is being shown
	Playlist showingPlaylist = null; // null if a tag is being shown
	Tag showingTag = null;			 // null if a playlist is being shown

	
	// Handlers
	List<TagsChangeHandler> tagsHandlers = new ArrayList<TagsChangeHandler>();
	List<PlaylistsChangeHandler> playlistsHandlers = new ArrayList<PlaylistsChangeHandler>();

	// Sending messages
	int currentScreen = 0;
	
	// Caches to archive albums and artists
	Cache<String, AlbumInfo> albumCache;
	Cache<String, ArtistInfo> artistCache;
	
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
		TagsMessage tm = new TagsMessage(this);
		SequenceHelper.sendJustOne(tm, RequestBuilder.GET);
		
		PlaylistsMessage pm = new PlaylistsMessage(this);
		SequenceHelper.sendJustOne(pm, RequestBuilder.GET);
	}
	
	// Cache
	public Cache<String, AlbumInfo> getAlbumCache() { return albumCache; }
	public Cache<String, ArtistInfo> getArtistCache() { return artistCache; }

	// Tags
	public void resetTagList() { tagList.clear(); tagIndex.clear(); }
	public void addTag(Tag t) {	tagList.add(t); tagIndex.put(t.getId(), t); }
	public void removeTag(Tag t) { tagList.remove(t); tagIndex.remove(t.getId()); }
	public void removeTag(String id) { removeTag(tagIndex.get(id)); }
	public void addTagsChangeHandler(TagsChangeHandler h) { tagsHandlers.add(h); }
	public List<Tag> getTagList() {	return tagList;	}
	public void fireTagsHandlers() {
		for (TagsChangeHandler h : tagsHandlers) {
			h.onTagsChange();
		}
	}
	
	// Playlists
	public void resetPlaylistList() { playlistList.clear(); playlistIndex.clear(); }
	public void addPlaylist(Playlist p) { playlistList.add(p); playlistIndex.put(p.getId(), p); }
	public void removePlaylist(Playlist p) { playlistList.remove(p); playlistIndex.remove(p.getId()); }
	public void removePlaylist(String id) { removePlaylist(playlistIndex.get(id)); }
	public void addPlaylistsChangeHandler(PlaylistsChangeHandler h) { playlistsHandlers.add(h);	}
	public List<Playlist> getPlaylistList() { return playlistList; }
	public void firePlaylistsHandlers() {
		for (PlaylistsChangeHandler h : playlistsHandlers) {
			h.onPlaylistsChange();
		}
	}

	public Playlist getShowingPlaylist() { return showingPlaylist; }
	public Tag getShowingTag() { return showingTag; }
	public void setShowing(Playlist p) { showingTag = null;	showingPlaylist = p; }
	public void setShowing(Tag t) { showingTag = t;	showingPlaylist = null; }
	public void setShowing() { showingTag = null; showingPlaylist = null; }
	public void setShowing(String id, boolean isTag) {
		if (isTag) {
			setShowing(tagIndex.get(id));
		} else {
			setShowing(playlistIndex.get(id));
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
	public void askForPlaylistSongs(String playlist, PlaylistHandler ph) {
		askForPlaylistSongs(0, Constants.NEXT_SONGS_TO_ASK, playlist, ph, false);
	}
	public void askForPlaylistSongs(String playlist, PlaylistHandler ph, boolean newScreen) {
		askForPlaylistSongs(0, Constants.NEXT_SONGS_TO_ASK, playlist, ph, newScreen);
	}
	public void askForPlaylistSongs(int from, int to, String playlist, PlaylistHandler ph) {
		askForPlaylistSongs(from, to, playlist, ph, false);
	}
	public void askForPlaylistSongs(int from, int to, String playlist, PlaylistHandler ph, boolean newScreen) {
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
		AddTagMessage atm = new AddTagMessage(name, this);
		SequenceHelper.sendJustOne(atm, RequestBuilder.PUT);
	}

	public void putNewPlaylist(String name) {
		AddPlaylistMessage apm = new AddPlaylistMessage(name, this);
		SequenceHelper.sendJustOne(apm, RequestBuilder.PUT);
	}
	
	public void deleteTag(String tagId) {
		DeleteTagMessage dtm = new DeleteTagMessage(tagId, this);
		SequenceHelper.sendJustOne(dtm, RequestBuilder.DELETE);
	}
	
	// addToPlaylist methods
	public void addToCurrentPlaylist(SongInfo s) {
		currentPlaylist.add(s);
	}
	
	public void addToCurrentPlaylist(List<SongInfo> songList) {
		for (SongInfo s : songList) {
			addToCurrentPlaylist(s);
		}
	}

}

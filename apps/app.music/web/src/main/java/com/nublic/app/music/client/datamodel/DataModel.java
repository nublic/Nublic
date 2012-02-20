package com.nublic.app.music.client.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.ParamsHashMap;
import com.nublic.app.music.client.datamodel.Controller.MyAlbumHandler;
import com.nublic.app.music.client.datamodel.handlers.AlbumHandler;
import com.nublic.app.music.client.datamodel.handlers.PlaylistsChangeHandler;
import com.nublic.app.music.client.datamodel.handlers.SongsChangeHandler;
import com.nublic.app.music.client.datamodel.handlers.StateChangeHandler;
import com.nublic.app.music.client.datamodel.handlers.TagsChangeHandler;
import com.nublic.app.music.client.datamodel.messages.AddPlaylistMessage;
import com.nublic.app.music.client.datamodel.messages.AddTagMessage;
import com.nublic.app.music.client.datamodel.messages.AlbumMessage;
import com.nublic.app.music.client.datamodel.messages.ArtistMessage;
import com.nublic.app.music.client.datamodel.messages.DeleteTagMessage;
import com.nublic.app.music.client.datamodel.messages.PlaylistContentMessage;
import com.nublic.app.music.client.datamodel.messages.PlaylistsMessage;
import com.nublic.app.music.client.datamodel.messages.TagsMessage;
import com.nublic.util.cache.Cache;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.DefaultComparator;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;
import com.nublic.util.messages.SequenceIgnorer;

public class DataModel {
	// Independent things
	List<Tag> tagList = new ArrayList<Tag>();
	List<Playlist> playlistList = new ArrayList<Playlist>();
	Playlist currentList = new Playlist(Constants.CURRENT_PLAYLIST_ID, Constants.CURRENT_PLAYLIST_NAME);
	HashMap<String, Tag> tagIndex = new HashMap<String, Tag>();
	HashMap<String, Playlist> playlistIndex = new HashMap<String, Playlist>();

	// Depending on what is being played
	Playlist currentPlayingList;
	int currentSongInPlaylist;
	
	// Depending on what is being shown
	State currentShowingState;
	Playlist showingPlaylist = null; // null if a tag is being shown
	Tag showingTag = null;			 // null if a playlist is being shown
	String showingArtistId = null;
	String showingAlbumId = null;
	List<Artist> artistList = new ArrayList<Artist>(); // These manage internal albums by themselves
	List<Album> albumList = new ArrayList<Album>(); // These manage internal songs by themselves 
	List<Song> songList = new ArrayList<Song>(); // These need following variables
	List<SongsChangeHandler> songHandlers = new ArrayList<SongsChangeHandler>();
	int numberOfSongs;
	
	// Handlers
	List<TagsChangeHandler> tagsHandlers = new ArrayList<TagsChangeHandler>();
	List<PlaylistsChangeHandler> playlistsHandlers = new ArrayList<PlaylistsChangeHandler>();
	List<StateChangeHandler> stateHandlers = new ArrayList<StateChangeHandler>();
	
	// Sending messages
	SequenceIgnorer<Message> messageSender = new SequenceIgnorer<Message>(DefaultComparator.INSTANCE);
	
	// Caches to archive albums and artists
	Cache<String, AlbumInfo> albumCache;
	Cache<String, ArtistInfo> artistCache;
	
	public DataModel() {
		// Initialize variables
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
		
		sendInitialMessages();
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
	
	// State
	public void setState(State s) { currentShowingState = s; }
	public State getState() { return currentShowingState; }
	public Playlist getShowingPlaylist() { return showingPlaylist; }
	public Tag getShowingTag() { return showingTag; }
	public String getShowingArtistId() { return showingArtistId; }
	public void setShowingArtistId(String showingArtistId) { this.showingArtistId = showingArtistId; }
	public String getShowingAlbumId() { return showingAlbumId; }
	public void setShowingAlbumId(String showingAlbumId) { this.showingAlbumId = showingAlbumId; }
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
	public void addStateChangeHandler(StateChangeHandler h) { stateHandlers.add(h);	}
	public void fireStateHandlers() {
		for (StateChangeHandler h : stateHandlers) {
			h.onStateChange();
		}
	}
	
	// Actual info to show
	public void clearArtistList() { artistList.clear(); }
	public void addArtist(Artist a) { artistList.add(a); }
	public List<Artist> getArtistList() { return artistList; }
	public void clearAlbumList() { albumList.clear(); }
	public void addAlbum(Album a) { albumList.add(a); }
	public List<Album> getAlbumList() { return albumList; }
	
	// songList handling
	public void addSong(int index, Song s) { songList.add(index, s); }
	public Song getSong(int index) { return songList.get(index); }
	public void addSongsChangeHandler(SongsChangeHandler handler) { songHandlers.add(handler); }
	public int getNumberOfSongs() { return numberOfSongs; } // Only to be called when fireSongHandlers have already been called
	public void fireSongHandlers(int from, int to, int total) {
		numberOfSongs = total;
		for (SongsChangeHandler h : songHandlers) {
			h.onSongsChange(from, to);
		}
	}
	
	// When URL changes this method is called
//	public void changeState(ParamsHashMap hmap) {
//		String collection = hmap.get(Constants.PARAM_COLLECTION);
//		String playlist   = hmap.get(Constants.PARAM_PLAYLIST);
//		String artist     = hmap.get(Constants.PARAM_ARTIST);
//		String album      = hmap.get(Constants.PARAM_ALBUM);
//		
//		if (collection != null) {
//			if (artist != null) {
//				askForAlbums(artist, collection);
//			} else if (album != null) {
//				askForSongs(album, collection);
//			} else {
//				askForArtists(collection);
//			}
//		} else if (playlist != null) {
//			askForPlaylistSongs(playlist);
//		} else {
//			// All music
//			if (artist != null) {
//				askForAlbums(artist);
//			} else if (album != null) {
//				askForSongs(album);
//			} else {
//				askForArtists();
//			}
//		}
//	}
	
	// methods to make requests to server in order to fill the data in the model
	public void askForSongs(String album) {
		askForSongs(album, null);		
	}

	public void askForSongs(String album, String collection) {
//		SongMessage sm = new SongMessage(this, null, album);
//		messageSender.send(sm, RequestBuilder.GET);
		setState(State.SONGS);
		setShowing(collection, true);
		setShowingAlbumId(album);
		fireStateHandlers();
	}

	public void askForAlbums(String artist) {
		askForAlbums(artist, null, null);
	}

	public void askForAlbums(String artist, String collection, AlbumHandler ah) {
		AlbumMessage am = new AlbumMessage(this, artist, collection);
		messageSender.send(am, RequestBuilder.GET);
	}

	public void askForArtists() {
		askForArtists(null);
	}
	
	public void askForArtists(String collection) {
		ArtistMessage am = new ArtistMessage(this, collection);
		messageSender.send(am, RequestBuilder.GET);
	}

	public void askForPlaylistSongs(String playlist) {
		if (playlist.equals(Constants.CURRENT_PLAYLIST_ID)) {
			setShowing(currentList);
			fireStateHandlers();
		} else {
			PlaylistContentMessage pcm = new PlaylistContentMessage(this, playlist);
			messageSender.send(pcm, RequestBuilder.GET);
		}
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
	
//	public void playPlaylist(Playlist p) {
//
//	}
}

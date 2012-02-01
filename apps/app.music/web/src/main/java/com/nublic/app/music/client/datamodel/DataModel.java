package com.nublic.app.music.client.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.http.client.RequestBuilder;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.ParamsHashMap;
import com.nublic.app.music.client.datamodel.handlers.PlaylistsChangeHandler;
import com.nublic.app.music.client.datamodel.handlers.StateChangeHandler;
import com.nublic.app.music.client.datamodel.handlers.TagsChangeHandler;
import com.nublic.app.music.client.datamodel.messages.AddPlaylistMessage;
import com.nublic.app.music.client.datamodel.messages.AddTagMessage;
import com.nublic.app.music.client.datamodel.messages.AlbumMessage;
import com.nublic.app.music.client.datamodel.messages.ArtistMessage;
import com.nublic.app.music.client.datamodel.messages.PlaylistContentMessage;
import com.nublic.app.music.client.datamodel.messages.PlaylistsMessage;
import com.nublic.app.music.client.datamodel.messages.TagsMessage;
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
	Playlist showingPlaylist = null; // null if a tag is being shown
	Tag showingTag = null;			 // null if a playlist is being shown
	State currentShowingState;
	List<Song> songList = new ArrayList<Song>();
	List<Album> albumList = new ArrayList<Album>();
	List<Artist> artistList = new ArrayList<Artist>();
	
	// Handlers
	List<TagsChangeHandler> tagsHandlers = new ArrayList<TagsChangeHandler>();
	List<PlaylistsChangeHandler> playlistsHandlers = new ArrayList<PlaylistsChangeHandler>();
	List<StateChangeHandler> stateHandlers = new ArrayList<StateChangeHandler>();
	
	// Sending messages
	SequenceIgnorer<Message> messageSender = new SequenceIgnorer<Message>(DefaultComparator.INSTANCE);
	
	public DataModel() {
		sendInitialMessages();
	}
	
	private void sendInitialMessages() {
		TagsMessage tm = new TagsMessage(this);
		SequenceHelper.sendJustOne(tm, RequestBuilder.GET);
		
		PlaylistsMessage pm = new PlaylistsMessage(this);
		SequenceHelper.sendJustOne(pm, RequestBuilder.GET);
	}

	// Tags
	public void resetTagList() { tagList.clear(); tagIndex.clear(); }
	public void addTag(Tag t) {	tagList.add(t); tagIndex.put(t.getId(), t); }
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
	
//	public void playPlaylist(Playlist p) {
//
//	}

	// When URL changes this method is called
	public void changeState(ParamsHashMap hmap) {
		String collection = hmap.get(Constants.PARAM_COLLECTION);
		String playlist   = hmap.get(Constants.PARAM_PLAYLIST);
		String artist     = hmap.get(Constants.PARAM_ARTIST);
		String album      = hmap.get(Constants.PARAM_ALBUM);
		
		if (collection != null) {
			if (artist != null) {
				askForAlbums(artist, collection);
			} else if (album != null) {
				askForSongs(album, collection);
			} else {
				askForArtists(collection);
			}
		} else if (playlist != null) {
			askForPlaylistSongs(playlist);
		} else {
			// All music
			if (artist != null) {
				askForAlbums(artist);
			} else if (album != null) {
				askForSongs(album);
			} else {
				askForArtists();
			}
		}
	}
	
	// methods to make requests to server in order to fill the data in the model
	private void askForSongs(String album) {
		askForSongs(album, null);		
	}

	private void askForSongs(String album, String collection) {
		// TODO Investigate if songs are going to fill the song list in this class or are going to be handled with an async data provider
	}

	private void askForAlbums(String artist) {
		askForAlbums(artist, null);
	}

	private void askForAlbums(String artist, String collection) {
		AlbumMessage am = new AlbumMessage(this, artist, collection);
		messageSender.send(am, RequestBuilder.GET);
	}

	private void askForArtists() {
		askForArtists(null);
	}
	
	private void askForArtists(String collection) {
		ArtistMessage am = new ArtistMessage(this, collection);
		messageSender.send(am, RequestBuilder.GET);
	}

	private void askForPlaylistSongs(String playlist) {
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
}

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
	
//	public void playPlaylist(Playlist p) {
//
//	}

	public void changeState(ParamsHashMap hmap) {
		String collection = hmap.get(Constants.PARAM_COLLECTION);
		String playlist   = hmap.get(Constants.PARAM_PLAYLIST);
		String artist     = hmap.get(Constants.PARAM_ARTIST);
		String album      = hmap.get(Constants.PARAM_ALBUM);
		
		if (collection != null) {
			if (artist != null) {
				
			} else if (album != null) {
				
			} else {
				askForArtists(collection);
			}
		} else if (playlist != null) {
			askForPlaylistSongs(playlist);
		} else {
			// All music
			if (artist != null) {
				
			} else if (album != null) {
				
			} else {
				askForArtists();
			}
		}
	}

	private void askForArtists() {
		ArtistMessage am = new ArtistMessage(this);
		messageSender.send(am, RequestBuilder.GET);	
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
}

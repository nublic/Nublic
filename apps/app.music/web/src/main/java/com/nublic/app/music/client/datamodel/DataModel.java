package com.nublic.app.music.client.datamodel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.http.client.RequestBuilder;
import com.nublic.app.music.client.datamodel.handlers.PlaylistsChangeHandler;
import com.nublic.app.music.client.datamodel.handlers.TagsChangeHandler;
import com.nublic.app.music.client.datamodel.messages.ArtistMessage;
import com.nublic.app.music.client.datamodel.messages.PlaylistMessage;
import com.nublic.app.music.client.datamodel.messages.TagMessage;
import com.nublic.util.messages.DefaultComparator;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;
import com.nublic.util.messages.SequenceIgnorer;

public class DataModel {

	// Independent things
	List<Tag> tagList = new ArrayList<Tag>();
	List<Playlist> playlistList = new ArrayList<Playlist>();
	Playlist currentList;
		
	// Depending on what is being played
	Playlist currentPlayingList;
	int currentSongInPlaylist;
	
	// Depending on what is being shown
	Playlist showingPlaylist = null; // null if a tag is being shown
	Tag showingTag = null;			 // null if a playlist is being shown
	State currentShowingState;
	List<Song> songList;
	List<Album> albumList;
	List<Artist> artistList;
	
	// Handlers
	List<TagsChangeHandler> tagsHandlers = new ArrayList<TagsChangeHandler>();
	List<PlaylistsChangeHandler> playlistsHandlers = new ArrayList<PlaylistsChangeHandler>();
	
	// Sending messages
	SequenceIgnorer<Message> messageSender = new SequenceIgnorer<Message>(DefaultComparator.INSTANCE);
	
	public DataModel() {
		sendInitialMessages();
	}
	
	private void sendInitialMessages() {
		TagMessage tm = new TagMessage(this);
		SequenceHelper.sendJustOne(tm, RequestBuilder.GET);
		
		PlaylistMessage pm = new PlaylistMessage(this);
		SequenceHelper.sendJustOne(pm, RequestBuilder.GET);
	}

	// Tags
	public void resetTagList() { tagList.clear(); }
	public void addTag(Tag t) {	tagList.add(t); }
	public void addTagsChangeHandler(TagsChangeHandler h) { tagsHandlers.add(h); }
	public List<Tag> getTagList() {	return tagList;	}
	public void fireTagsHandlers() {
		for (TagsChangeHandler h : tagsHandlers) {
			h.onTagsChange();
		}
	}
	
	// Playlists
	public void resetPlaylistList() { playlistList.clear(); }
	public void addPlaylist(Playlist p) { playlistList.add(p); }
	public void addPlaylistsChangeHandler(PlaylistsChangeHandler h) { playlistsHandlers.add(h);	}
	public List<Playlist> getPlaylistList() { return playlistList; }
	public void firePlaylistsHandlers() {
		for (PlaylistsChangeHandler h : playlistsHandlers) {
			h.onPlaylistsChange();
		}
	}
	
	// State
	public State getCurrentShowingState() { return currentShowingState; }
	public Playlist getShowingPlaylist() { return showingPlaylist; }
	public Tag getShowingTag() { return showingTag; }
	public void setShowing(Playlist p) {

	}
	public void setShowing(Tag t) {

	}


	public void showTag(Tag t) {
		ArtistMessage am = new ArtistMessage(t);
		messageSender.send(am, RequestBuilder.GET);
	}
	
	public void showPlaylist(Playlist p) {
//		ArtistMessage am = new ArtistMessage(p);
//		messageSender.send(am, RequestBuilder.GET);
	}
	
	public void playPlaylist(Playlist p) {

	}
}

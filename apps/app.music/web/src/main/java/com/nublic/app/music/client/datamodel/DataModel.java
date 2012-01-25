package com.nublic.app.music.client.datamodel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.http.client.RequestBuilder;
import com.nublic.app.music.client.datamodel.handlers.PlaylistsChangeHandler;
import com.nublic.app.music.client.datamodel.handlers.TagsChangeHandler;
import com.nublic.app.music.client.datamodel.messages.PlaylistMessage;
import com.nublic.app.music.client.datamodel.messages.TagMessage;
import com.nublic.util.messages.SequenceHelper;

public class DataModel {

	// Independent things
	List<Tag> tagList = new ArrayList<Tag>();
	List<Playlist> playlistList = new ArrayList<Playlist>();
	Playlist currentList;
		
	// Depending on what is being played
	int currentPlayingListIndex; // -1 means currentList, any other number is the index in playlistList
	int currentSongInPlaylist;
	
	// Depending on what is being shown
	int currentShowingIndex;
	State currentShowingState;
	List<Song> songList;
	List<Album> albumList;
	List<Artist> artistList;
	
	// Handlers
	List<TagsChangeHandler> tagsHandlers = new ArrayList<TagsChangeHandler>();
	List<PlaylistsChangeHandler> playlistsHandlers = new ArrayList<PlaylistsChangeHandler>();
	
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
}

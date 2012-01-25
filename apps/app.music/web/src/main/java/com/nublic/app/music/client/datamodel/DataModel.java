package com.nublic.app.music.client.datamodel;

import java.util.ArrayList;
import java.util.List;

import com.nublic.app.music.client.datamodel.handlers.TagsChangeHandler;

public class DataModel {

	// Independent things
	List<Tag> tagList;
	List<Playlist> playlistList;
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
	
	public DataModel() {
		
	}

	// Tags
	public void resetTagList() {
		tagList.clear();
	}
	
	public void addTag(Tag t) {
		tagList.add(t);
	}
	
	public void addTagsChangeHandler(TagsChangeHandler h) {
		tagsHandlers.add(h);
	}
	
	public List<Tag> getTagList() {
		return tagList;
	}
	
	public void fireTagsHandlers() {
		for (TagsChangeHandler h : tagsHandlers) {
			h.onTagsChange();
		}
	}
}

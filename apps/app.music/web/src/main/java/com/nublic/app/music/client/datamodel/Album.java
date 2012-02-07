package com.nublic.app.music.client.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.nublic.app.music.client.datamodel.handlers.SongsChangeHandler;

//album  ::= { "id" : $album-id,
//        "name": $name,
//        "songs": $number_of_songs,
//        $extra_info }

public class Album {
	String id;
	String name;
	int numberOfSongs;

	List<Song> songList;
	HashMap<Integer, Song> songMap;
	List<SongsChangeHandler> songHandlers;
	
	String inCollection;
	Artist inArtist;

	public Album(String id, String name, int numberOfSongs) {
		this(id, name, numberOfSongs, null);
	}
	
	public Album(String id, String name, int numberOfSongs, String inCollection) {
		this(id, name, numberOfSongs, inCollection, null);
	}
	
	public Album(String id, String name, int numberOfSongs, String inCollection, Artist inArtist) {
		this.id = id;
		this.name = name;
		this.numberOfSongs = numberOfSongs;
		this.inCollection = inCollection;
		this.inArtist = inArtist;
	}

	// Getters and setters
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public int getNumberOfSongs() { return numberOfSongs; }
	public void setNumberOfSongs(int numberOfSongs) { this.numberOfSongs = numberOfSongs; }
	public String getInCollection() { return inCollection; }
	public void setInCollection(String inCollection) { this.inCollection = inCollection; }
	public Artist getInArtist() { return inArtist; }
	public void setInArtist(Artist inArtist) { this.inArtist = inArtist; }
	
	public void prepareToAddSongs() {
		songList = new ArrayList<Song>(numberOfSongs);
		songMap = new HashMap<Integer, Song>(numberOfSongs);
		songHandlers = new ArrayList<SongsChangeHandler>();
	}
	
	public void addSong(int index, Song s) {
		songList.set(index, s);
		songMap.put(index, s);
	}
	
	public Song getSong(int index) {
		return songList.get(index);
	}

	public void addSongsChangeHandler(SongsChangeHandler handler) {
		songHandlers.add(handler);
	}
	
	public void fireSongHandlers(int from, int to) {
		for (SongsChangeHandler h : songHandlers) {
			h.onSongsChange(from, to);
		}
	}

}

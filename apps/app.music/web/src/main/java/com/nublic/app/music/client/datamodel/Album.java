package com.nublic.app.music.client.datamodel;

import java.util.ArrayList;
import java.util.List;

import com.nublic.app.music.client.datamodel.handlers.SongsChangeHandler;

//album  ::= { "id" : $album-id,
//        "name": $name,
//        "songs": $number_of_songs,
//        $extra_info }
public class Album {
	AlbumInfo info;

	Song[] songList;
	List<SongsChangeHandler> songHandlers;
	String inCollection;
	Artist inArtist;

	public Album(AlbumInfo info) {
		this(info, null, null);
	}
	
	public Album(AlbumInfo info, String inCollection) {
		this(info, inCollection, null);
	}
	
	public Album(AlbumInfo info, String inCollection, Artist inArtist) {
		this.info = info;
		this.inCollection = inCollection;
		this.inArtist = inArtist;
	}

	// Getters and setters
	public AlbumInfo getInfo() { return info; }
	public void setInfo(AlbumInfo info) { this.info = info; }
	public String getInCollection() { return inCollection; }
	public void setInCollection(String inCollection) { this.inCollection = inCollection; }
	public Artist getInArtist() { return inArtist; }
	public void setInArtist(Artist inArtist) { this.inArtist = inArtist; }
	
	public void prepareToAddSongs() {
		songList = new Song[info.getNumberOfSongs()];
		songHandlers = new ArrayList<SongsChangeHandler>();
	}
	
	public void addSong(int index, Song s) {
		songList[index] = s;
	}
	
	public Song getSong(int index) {
		return songList[index];
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

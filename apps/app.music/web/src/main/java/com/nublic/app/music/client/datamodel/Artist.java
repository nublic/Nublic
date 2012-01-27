package com.nublic.app.music.client.datamodel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.http.client.RequestBuilder;
import com.nublic.app.music.client.datamodel.handlers.AlbumsChangeHandler;
import com.nublic.app.music.client.datamodel.messages.AlbumMessage;
import com.nublic.util.messages.SequenceHelper;

//artist ::= { "id" : $artist-id,
//        "name": $name,
//        "discs": $number_of_discs,
//        "songs": $number_of_songs,
//        $extra_info }

public class Artist {
	String id;
	String name;
	int numberOfDiscs;
	int numberOfSongs;
	AlbumsChangeHandler handler = null;
	List<Album> albumList = new ArrayList<Album>();

	public Artist(String id, String name, int numberOfDiscs, int numberOfSongs) {
		this.id = id;
		this.name = name;
		this.numberOfDiscs = numberOfDiscs;
		this.numberOfSongs = numberOfSongs;
	}
	
	public Artist(String id, String name, int numberOfDiscs, int numberOfSongs, AlbumsChangeHandler handler) {
		this(id, name, numberOfDiscs, numberOfSongs);
		this.handler = handler;

		askForAlbums();
	}

	public void setAlbumsHandler(AlbumsChangeHandler h) {
		handler = h;
	}
	
	public void askForAlbums() {
		AlbumMessage am = new AlbumMessage(this);
		SequenceHelper.sendJustOne(am, RequestBuilder.GET);
	}
	
	public void fireAlbumsHandler() {
		if (handler != null) {
			handler.onAlbumsChange();
		}
	}

	// Getters and setters
	public String getId() {	return id; }
	public void setId(String id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public int getNumberOfDiscs() { return numberOfDiscs; }
	public void setNumberOfDiscs(int numberOfDiscs) { this.numberOfDiscs = numberOfDiscs; }
	public int getNumberOfSongs() { return numberOfSongs; }
	public void setNumberOfSongs(int numberOfSongs) { this.numberOfSongs = numberOfSongs; }
	public List<Album> getAlbumList() {	return albumList; }
	public void clearAlbumList() { albumList.clear(); }
	public void addAlbum(Album a) { albumList.add(a); }
	
}

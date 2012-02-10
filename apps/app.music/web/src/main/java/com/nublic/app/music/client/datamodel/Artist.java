package com.nublic.app.music.client.datamodel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.http.client.RequestBuilder;
import com.nublic.app.music.client.datamodel.handlers.AlbumsChangeHandler;
import com.nublic.app.music.client.datamodel.messages.AlbumMessage;
import com.nublic.util.cache.Cache;
import com.nublic.util.messages.SequenceHelper;

//artist ::= { "id" : $artist-id,
//        "name": $name,
//        "discs": $number_of_discs,
//        "songs": $number_of_songs,
//        $extra_info }

public class Artist {
	ArtistInfo info;
	AlbumsChangeHandler handler = null;
	List<Album> albumList = new ArrayList<Album>();
	
	String inCollection;

	public Artist(ArtistInfo info) {
		this(info, null);
	}
	
	public Artist(ArtistInfo info, String inCollection) {
		this.info = info;
		this.inCollection = inCollection;
	}
	
	public void setAlbumsHandler(AlbumsChangeHandler h) {
		handler = h;
	}
	
	public void askForAlbums(Cache<String, AlbumInfo> cache) {
		AlbumMessage am = new AlbumMessage(this, cache);
		SequenceHelper.sendJustOne(am, RequestBuilder.GET);
	}
	
	public void fireAlbumsHandler() {
		if (handler != null) {
			handler.onAlbumsChange();
		}
	}

	// Getters and setters
	public ArtistInfo getInfo() { return info; }
	public void setInfo(ArtistInfo info) { this.info = info; }
	public List<Album> getAlbumList() {	return albumList; }
	public void clearAlbumList() { albumList.clear(); }
	public void addAlbum(Album a) { albumList.add(a); }
	public String getInCollection() { return inCollection; }
	public void setInCollection(String inCollection) { this.inCollection = inCollection; }
	
	
}

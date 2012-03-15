package com.nublic.app.music.client.datamodel;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.nublic.app.music.client.Constants;

//album  ::= { "id" : $album-id,
//        "name": $name,
//        "songs": $number_of_songs,
//        $extra_info }

public class AlbumInfo {
	String id;
	String name;
	int numberOfSongs;
	List<String> artistList;

	public AlbumInfo(String id, String name, int numberOfSongs, List<String> artistList) {
		this.id = id;
		this.name = name;
		this.numberOfSongs = numberOfSongs;
		this.artistList = artistList;
	}

	// Getters and setters
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public int getNumberOfSongs() { return numberOfSongs; }
	public void setNumberOfSongs(int numberOfSongs) { this.numberOfSongs = numberOfSongs; }
	public List<String> getArtistList() { return artistList; }
	
	public String getImageUrl() {
		return AlbumInfo.getAlbumImageUrl(id);
	}
	
	public static String getAlbumImageUrl(String albumId) {
		// building imageUrl as /album-art/:album-id
		StringBuilder imageUrl = new StringBuilder();
		imageUrl.append(GWT.getHostPageBaseURL());
		imageUrl.append("server/album-art/");
		imageUrl.append(albumId);
		imageUrl.append(".png");
		
		return imageUrl.toString();
	}
	
	public String getTargetHistoryToken(String collectionId) {
		final StringBuilder target = new StringBuilder();		
		if (collectionId != null) {
			target.append(Constants.PARAM_COLLECTION);
			target.append("=");
			target.append(collectionId);
			target.append("&");
		}
		target.append(Constants.PARAM_ALBUM);
		target.append("=");
		target.append(id);
		
		return target.toString();
	}
}

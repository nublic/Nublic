package com.nublic.app.music.client.datamodel;

import java.util.List;

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
}

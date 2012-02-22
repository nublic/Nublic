package com.nublic.app.music.client.datamodel;

//artist ::= { "id" : $artist-id,
//        "name": $name,
//        "discs": $number_of_discs,
//        "songs": $number_of_songs,
//        $extra_info }

public class ArtistInfo {
	String id;
	String name;
	int numberOfAlbums;
	int numberOfSongs;

	public ArtistInfo(String id, String name, int numberOfAlbums, int numberOfSongs) {
		this.id = id;
		this.name = name;
		this.numberOfAlbums = numberOfAlbums;
		this.numberOfSongs = numberOfSongs;
	}

	// Getters and setters
	public String getId() {	return id; }
	public void setId(String id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public int getNumberOfAlbums() { return numberOfAlbums; }
	public void setNumberOfAlbums(int numberOfAlbums) { this.numberOfAlbums = numberOfAlbums; }
	public int getNumberOfSongs() { return numberOfSongs; }
	public void setNumberOfSongs(int numberOfSongs) { this.numberOfSongs = numberOfSongs; }

}

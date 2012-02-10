package com.nublic.app.music.client.datamodel;

//artist ::= { "id" : $artist-id,
//        "name": $name,
//        "discs": $number_of_discs,
//        "songs": $number_of_songs,
//        $extra_info }

public class ArtistInfo {
	String id;
	String name;
	int numberOfDiscs;
	int numberOfSongs;

	public ArtistInfo(String id, String name, int numberOfDiscs, int numberOfSongs) {
		this.id = id;
		this.name = name;
		this.numberOfDiscs = numberOfDiscs;
		this.numberOfSongs = numberOfSongs;
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

}

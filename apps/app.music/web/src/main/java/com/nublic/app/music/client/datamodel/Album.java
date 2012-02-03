package com.nublic.app.music.client.datamodel;

//album  ::= { "id" : $album-id,
//        "name": $name,
//        "songs": $number_of_songs,
//        $extra_info }

public class Album {
	String id;
	String name;
	int numberOfSongs;

	String inCollection;

	public Album(String id, String name, int numberOfSongs) {
		this(id, name, numberOfSongs, null);
	}
	
	public Album(String id, String name, int numberOfSongs, String inCollection) {
		this.id = id;
		this.name = name;
		this.numberOfSongs = numberOfSongs;
		this.inCollection = inCollection;
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

}

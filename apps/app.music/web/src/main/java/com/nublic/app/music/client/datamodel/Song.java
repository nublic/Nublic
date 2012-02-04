package com.nublic.app.music.client.datamodel;


//song   ::= { "id": $song-id,
//        "title": $title,
//        "artist-id": $artist-id,
//        "album-id": $album-id,
//        $extra_info }
public class Song {
	String id;
	String title;
	String artistId;
	String albumId;
	
	public Song(String id, String title, String artistId, String albumId) {
		this.id = id;
		this.title = title;
		this.artistId = artistId;
		this.albumId = albumId;
	}

	// Getters and setters
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	public String getArtistId() { return artistId; }
	public void setArtistId(String artistId) { this.artistId = artistId; }
	public String getAlbumId() { return albumId; }
	public void setAlbumId(String albumId) { this.albumId = albumId; }
	
}

package com.nublic.app.music.client.datamodel;

import com.google.gwt.i18n.client.NumberFormat;

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
	int track;
	int length;
	
	public Song(String id, String title, String artistId, String albumId, int track, int length) {
		this.id = id;
		this.title = title;
		this.artistId = artistId;
		this.albumId = albumId;
		this.track = track;
		this.length = length;
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
	public int getTrack() { return track; }
	public void setTrack(int track) { this.track = track; }
	public int getLength() { return length; }
	public void setLength(int length) { this.length = length; }
	
	public String getFormattedLength() {
		StringBuilder sb = new StringBuilder();
		sb.append((int)(length/60));
		sb.append(":");
		sb.append(NumberFormat.getFormat("00").format(length%60));
		return sb.toString();
//		return String.format("%d:%d", getLenght()/60, getLenght()%60);
	}
	
}

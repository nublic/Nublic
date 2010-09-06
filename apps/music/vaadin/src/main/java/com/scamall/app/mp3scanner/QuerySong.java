package com.scamall.app.mp3scanner;

import org.apache.solr.common.SolrDocument;

public class QuerySong {
	
	private String title;
	private String year;
	private Integer trackNumber;
	private String genre;
	private String artist;
	private String album;

	public QuerySong(Song s) {
		title = s.getTitle();
		genre = s.getGenre();
		year = s.getYear();
		trackNumber = s.getTrackNumber();
		artist = s.getArtist().getName();
		album = s.getAlbum().getTitle();		
	}
	
	public QuerySong(SolrDocument doc) {
		title = (String)doc.getFieldValue("title");
		genre = (String)doc.getFieldValue("genre");
		year = (String)doc.getFieldValue("year");
		trackNumber = (Integer)doc.getFieldValue("trackNumber");
		artist = (String)doc.getFieldValue("artist");
		album = (String)doc.getFieldValue("album");
	}

	public String getTitle() {
		return title;
	}

	public String getYear() {
		return year;
	}

	public Integer getTrackNumber() {
		return trackNumber;
	}

	public String getGenre() {
		return genre;
	}

	public String getArtist() {
		return artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public void setTrackNumber(Integer trackNumber) {
		this.trackNumber = trackNumber;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public void setAlbum(String album) {
		this.album = album;
	}
}

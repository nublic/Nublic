package com.scamall.app.mp3scanner;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.solr.common.SolrInputDocument;
import org.jdom.Element;

public class Song {
	private static final String TAG_XML = "Song";
	private String title;
	private String year;
	private Integer trackNumber;
	private String genre;
	private String path;
	private Artist artist;
	private Album album;
	
	public Song(String t, String g, String y, int track, String p,
			    Artist ar, Album al) {
		title = t;
		genre = g;
		year = y;
		trackNumber = track;
		path = p;
		artist = ar;
		album = al;		
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

	public String getPath() {
		return path;
	}

	public Artist getArtist() {
		return artist;
	}

	public Album getAlbum() {
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

	public void setPath(String path) {
		this.path = path;
	}

	public void setArtist(Artist artist) {
		this.artist = artist;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public Element toXML() {
        Element e = new Element(TAG_XML);
        e.setAttribute("title", title);
        e.setAttribute("year", year);
        e.setAttribute("track_number", trackNumber.toString());
        e.setAttribute("genre", genre);
        e.setAttribute("path", path);
        return e;
    }
    
	public Collection<SolrInputDocument> toSOLR() {
		Collection<SolrInputDocument> lista = new ArrayList<SolrInputDocument>();

		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("type","music");
	    doc.addField("title",title);
	    doc.addField("year",year);
	    doc.addField("artist",artist.getName());
	    doc.addField("album",album.getTitle());
	    doc.addField("trackNumber",Integer.valueOf(trackNumber));
	    doc.addField("genre", genre);
	    doc.addField("path", path);
	    
	    lista.add(doc);
		
		return lista;
	}
}

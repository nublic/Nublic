package com.scamall.app.mp3scanner;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.solr.common.SolrInputDocument;
import org.jdom.Element;

public class Album {
	private static final String TAG_XML = "Album";
	private String title;
	private String year;
	private Artist artist;
	private ArrayList<Song> songs;
	
	public Album(String t, String y, Artist a) {
		title = t;
		year = y;
		artist = a;
		songs = new ArrayList<Song>();
	}
	
	public Album addSong(String song, String genre,
                   String year, Integer track, String path) {
		songs.add(new Song(song,genre,year,track,path,artist,this));
		return this;
	}
	
    public String getTitle() {
		return title;
	}

	public String getYear() {
		return year;
	}

	public Artist getArtist() {
		return artist;
	}

	public ArrayList<Song> getSongs() {
		return songs;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public void setArtist(Artist artist) {
		this.artist = artist;
	}

	public void setSongs(ArrayList<Song> songs) {
		this.songs = songs;
	}

	public Element toXML() {
        Element e = new Element(TAG_XML);
        e.setAttribute("title", title);
        e.setAttribute("year", year);
        for (Song s : songs)
        	e.addContent(s.toXML());
        return e;
    }
	
	public Collection<SolrInputDocument> toSOLR() {
		Collection<SolrInputDocument> lista = new ArrayList<SolrInputDocument>();
		for (Song s : songs)
			lista.addAll(s.toSOLR());
		
/*		SolrInputDocument doc = new SolrInputDocument();
	    doc.addField("title",title);
	    doc.addField("year",year);
		lista.add(doc);*/
		
		return lista;
	}
}

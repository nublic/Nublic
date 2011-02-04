package com.scamall.app.mp3scanner;

import java.util.ArrayList;
import java.util.Collection;

import org.jdom.Element;

import org.apache.solr.common.SolrInputDocument;


/**
 * 
 * @author sergio
 *
 */
public class Artist {
	private static final String TAG_XML = "Artist";
	private String name;
	private ArrayList<Album> albums;
	
	public Artist(String name) {
		this.name = name;
		this.albums = new ArrayList<Album>();
	}
	
	public Artist addSong(String album, String song, String genre,
			              String year, Integer track, String path) {
		for (Album a : albums) {
			if (a.getTitle().equals(album)) {
				a.addSong(song,genre,year,track,path);
				return this;
			}
		}
		
		albums.add(new Album(album,year,this).addSong(song,genre,year,track,path));
		return this;
	}
	
	public ArrayList<Song> getSongs() {
		ArrayList<Song> songs = new ArrayList<Song>();
		
		for (Album a : albums)
			songs.addAll(a.getSongs());
		
		return songs;
	}
	
    public String getName() {
		return name;
	}

	public ArrayList<Album> getAlbums() {
		return albums;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAlbums(ArrayList<Album> albums) {
		this.albums = albums;
	}

	public Element toXML() {
        Element e = new Element(TAG_XML);
        e.setAttribute("name", name);
        for (Album a : albums)
        	e.addContent(a.toXML());
        return e;
    }
	
	public Collection<SolrInputDocument> toSOLR() {
		Collection<SolrInputDocument> lista = new ArrayList<SolrInputDocument>();
		for (Album a : albums)
			lista.addAll(a.toSOLR());
		
/*		SolrInputDocument doc = new SolrInputDocument();
	    doc.addField("name",name);
		lista.add(doc);*/
		
		return lista;
	}

	@Override
	public boolean equals(Object obj) {
		return ((obj instanceof Artist) &&
				(name.equals(((Artist)obj).getName())));
	}

}

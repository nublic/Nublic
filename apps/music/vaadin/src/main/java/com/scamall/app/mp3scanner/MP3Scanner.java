package com.scamall.app.mp3scanner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import net.roarsoftware.lastfm.*;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import org.farng.mp3.MP3File;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.ID3v1;

import org.cmc.music.myid3.*;
import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;

import org.blinkenlights.jid3.MediaFile;
import org.blinkenlights.jid3.v1.ID3V1Tag;
import org.blinkenlights.jid3.v2.ID3V2Tag;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

public class MP3Scanner {
	private static final String lastfmKey = "ba370b366694dd4c0bb2051be771a109";
	private static final String lastfmSecret = "bda3ea3eec5cb13f077ef6ce9f5a6107";
	private ArrayList<Artist> artists = new ArrayList<Artist>();
	
	private void getTags(File f) {
		getTags_lastfm(f);
//		getTags_jaudiotagger(f);
//		getTags_jid3lib(f);
//		getTags_myid3(f);
//		getTags_JID3(f);
	}
	
	private void newSong(String artist,String album,String song,String genre,
						 String year,Integer track,String path) {
		for (Artist a : artists) {
			if (a.getName().equals(artist)) {
				a.addSong(album,song,genre,year,track,path);
				return;
			}
		}
		artists.add(new Artist(artist).addSong(album,song,genre,year,track,path));
	}
	
	private void getTags_lastfm(File f) {
		System.out.println("Procesando " + f.getName() + "... ");
		
		String[] command = {"../lib/lastfmfpclient", f.getAbsolutePath() };
		try
		{
			final Process process = Runtime.getRuntime().exec(command);
			new Thread(){
				public void run(){
					try{
						InputStream is = process.getInputStream();
						FileOutputStream os = new FileOutputStream(new File("tmp/temp.xml"));
						byte[] buffer = new byte[1024];
						for(int count = 0; (count = is.read(buffer)) >= 0;){
							os.write(buffer, 0, count);
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			}.start();
			new Thread(){
				public void run(){
					try{
						InputStream is = process.getErrorStream();
						byte[] buffer = new byte[1024];
						for(int count = 0; (count = is.read(buffer)) >= 0;){
							System.err.write(buffer, 0, count);
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			}.start();

			process.waitFor();
//			int returnCode = 
//			System.out.println("Return code = " + returnCode);
		}
		catch (Exception e){
			e.printStackTrace();
		}		

		// Leemos la salida del programa
        SAXBuilder parser = new SAXBuilder();

		try {
			Caller.getInstance().setUserAgent("sergomezcol");
			Caller.getInstance().setDebugMode(true);

	        Document doc = parser.build("tmp/temp.xml");
	        Element track = doc.getRootElement().getChild("tracks").getChild("track");

	        Track trackInfo;
			String artist = track.getChild("artist").getChildText("name");
			String title = track.getChildText("name");
			String mbid = track.getChildText("mbid");
			String year = "";
			String genre = "";
			Integer numTrack = 0;
			
			if (!mbid.equals(""))
				trackInfo = Track.getInfo(null,mbid,lastfmKey);
			else
				trackInfo = Track.getInfo(artist,title,lastfmKey);
	        
			String album = trackInfo.getAlbum();
			if (album != null) {
				net.roarsoftware.lastfm.Album albumInfo = net.roarsoftware.lastfm.Album.getInfo(artist,album,lastfmKey);
				Date date = albumInfo.getReleaseDate();
				if (date != null) {
					SimpleDateFormat formato = new SimpleDateFormat("yyyy");
					year = formato.format(date);
				}
			} else {
				album = "";
			}

			newSong(artist,album,title,genre,year,numTrack,f.getAbsolutePath());
	    } catch (Exception ex) {
			System.out.println("Error al obtener la información");
			ex.printStackTrace();
			return;
		}
    }

	private void getTags_jaudiotagger(File f) {
		try {
			AudioFile audio = AudioFileIO.read(f);
			Tag tag = audio.getTag();
				
			String artist = tag.getFirst(FieldKey.ARTIST);
			String album = tag.getFirst(FieldKey.ALBUM);
			String title = tag.getFirst(FieldKey.TITLE);
			String year = tag.getFirst(FieldKey.YEAR);
			String genre = tag.getFirst(FieldKey.GENRE);
			Integer track = Integer.valueOf(tag.getFirst(FieldKey.TRACK));
			newSong(artist,album,title,genre,year,track,f.getAbsolutePath());
		} catch (Exception ex) {
			System.out.println("Error al leer los tags MP3 en " + f.getAbsolutePath());
			newSong("","",f.getName(),"","",0,f.getAbsolutePath());
		}
	}

		
	private void getTags_jid3lib(File f) {
		try {
			MP3File mp3 = new MP3File(f);
			

			String artistName = "";
			String album = "";
			String title = f.getName();
			String genre = "";
			String year = "";
			Integer track = 0;

			if (mp3.hasID3v1Tag()) {
				ID3v1 tag = mp3.getID3v1Tag();
				artistName = tag.getLeadArtist();
				album = tag.getAlbumTitle();
				title = tag.getSongTitle();
				genre = tag.getSongGenre();
				year = tag.getYearReleased();
				track = Integer.valueOf(tag.getTrackNumberOnAlbum());			
			} else if (mp3.hasID3v2Tag()) {
				AbstractID3v2 tag = mp3.getID3v2Tag();
				artistName = tag.getLeadArtist();
				album = tag.getAlbumTitle();
				title = tag.getSongTitle();
				genre = tag.getSongGenre();
				year = tag.getYearReleased();
				track = Integer.valueOf(tag.getTrackNumberOnAlbum());
			}
			newSong(artistName,album,title,genre,year,track,f.getAbsolutePath());
		} catch (IOException ex) {
			System.out.println("Error al leer el fichero " + f.getAbsolutePath());
		} catch (Exception ex) {
			System.out.println("Error al leer los tags MP3 en " + f.getAbsolutePath());
			newSong("","",f.getName(),"","",0,f.getAbsolutePath());
		}
	}
	
	private void getTags_myid3(File f) {
		try {
			MusicMetadataSet f_set = new MyID3().read(f); // read metadata
			if (f_set == null) // perhaps no metadata
				return;
	
			IMusicMetadata metadata = f_set.getSimplified();
			String artistName = metadata.getArtist();  
			String album = metadata.getAlbum();  
			String song = metadata.getSongTitle();
			String year = metadata.getYear().toString();
			String genre = metadata.getGenreName();			
			Integer track = (Integer)metadata.getTrackNumberNumeric();
			newSong(artistName,album,song,genre,year,track,f.getAbsolutePath());
		} catch (IOException ex) {
			System.out.println("Error al leer el fichero " + f.getAbsolutePath());
		} catch (Exception ex) {
			System.out.println("Error al leer los tags MP3 en " + f.getAbsolutePath());
			newSong("","",f.getName(),"","",0,f.getAbsolutePath());
		}
	}
	
	private void getTags_JID3(File f) {
		MediaFile mp3  = new org.blinkenlights.jid3.MP3File(f);
		
		try {
			String artist = "";
			String album = "";
			String year = "";
			Integer track = 0;
			String genre = "";
			String title = f.getName();
			
			org.blinkenlights.jid3.ID3Tag[] tags = mp3.getTags();
			for (org.blinkenlights.jid3.ID3Tag t : tags) {
				if (t instanceof ID3V2Tag) {
					ID3V2Tag v2tag = (ID3V2Tag) t;
					if (v2tag.getArtist() != null)
						artist = v2tag.getArtist();
					if (v2tag.getAlbum() != null)
						album = v2tag.getAlbum();
					if (v2tag.getYear() > 0)
						year = Integer.toString(v2tag.getYear());
					if (v2tag.getGenre() != null)
						genre = v2tag.getGenre();
					if (v2tag.getTrackNumber() > 0)
						track = v2tag.getTrackNumber();
					if (v2tag.getTitle() != null)
						title = v2tag.getTitle();
				} else if (t instanceof ID3V1Tag) {
					ID3V1Tag v1tag = (ID3V1Tag) t;
					if (v1tag.getArtist() != null)
						artist = v1tag.getArtist();
					if (v1tag.getAlbum() != null)
						album = v1tag.getAlbum();
					if (v1tag.getYear() != null)
						year = v1tag.getYear();
					if (v1tag.getGenre() != null) {
						org.blinkenlights.jid3.v1.ID3V1Tag.Genre g = v1tag.getGenre();
						genre = g.toString();
					}
					if (v1tag.getTitle() != null)
						title = v1tag.getTitle();
				}
			}
			newSong(artist,album,title,genre,year,track,f.getAbsolutePath());
		} catch (Exception ex) {
			System.out.println("Error al leer los tags MP3 en " + f.getAbsolutePath());
			newSong("","",f.getName(),"","",0,f.getAbsolutePath());
			return;
		}
	}

	
	public void scan(String path, Boolean recursive) {
		File parent = new File(path);
		for (File f : parent.listFiles()) {
			if (f.isDirectory() && recursive)
				scan(f.getAbsolutePath(),recursive);
			else {
				/*String filename = f.getName();
				String ext = filename.substring(filename.lastIndexOf('.')+1,
									            filename.length());
				if (ext.equals("mp3"))
					newSong(f);*/
				if (f.getName().toLowerCase().endsWith(".mp3"))
					getTags(f);
			}
		}
	}
	
	public ArrayList<Song> getSongs() {
		ArrayList<Song> songs = new ArrayList<Song>();
		
		for (Artist a : artists)
			songs.addAll(a.getSongs());
		
		return songs;
	}
	
	public void toXML() throws Exception {
		Element e = new Element("Artists");
		for (Artist a : artists)
			e.addContent(a.toXML());
		
        Document doc = new Document(e);
        XMLOutputter salida = new XMLOutputter();
        salida.setFormat(Format.getPrettyFormat());
        FileOutputStream fichero = new FileOutputStream("tmp/mp3.xml");
        salida.output(doc,fichero);
        fichero.flush();
        fichero.close();
	}
	
	public void toSOLR() {
		try {
			SolrServer server = new CommonsHttpSolrServer("http://localhost:8082/solr");
			Collection<SolrInputDocument> lista = new ArrayList<SolrInputDocument>();
			for (Artist a : artists)
				lista.addAll(a.toSOLR());
			
			server.deleteByQuery("type:music");
			server.add(lista);
			server.commit();
		} catch (IOException ex) {
			System.out.println("Error de I/O");
		} catch (SolrServerException ex) {
			System.out.println("Error en el servidor SOLR");			
		}
	}
	
    public static void main(String[] args) throws Exception {
    	MP3Scanner m = new MP3Scanner();
    	m.scan("/media/windows/Users/Sergio/Desktop/Música/AyrtonSennaEP",true);
    	m.toXML();
//    	m.toSOLR();
    	System.out.println("OK!");
    }
}
    


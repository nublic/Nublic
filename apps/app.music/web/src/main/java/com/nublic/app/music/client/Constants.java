package com.nublic.app.music.client;

public class Constants {

	public static final String PARAM_COLLECTION = "Collection";
	public static final String PARAM_PLAYLIST = "Playlist";
	public static final String PARAM_ARTIST = "Artist";
	public static final String PARAM_ALBUM = "Album";
	public static final String ORDER_ALPHA = "alpha";				// alpha -> alphabetical by song title
	public static final String ORDER_ARTIST_ALPHA = "artist_alpha"; // artist_alpha -> first artist name, then song title
	public static final String ORDER_ARTIST_ALBUM = "artist_album"; // artist_album -> first artist name, then album name, then disc_no, then track
	public static final String ORDER_ALBUM = "album";				// album -> first album name, then disc_no, then track
	public static final String CURRENT_PLAYLIST_ID = "2thTGku28JHGcF823dYAt35jJvAx9jy9";
	public static final String CURRENT_PLAYLIST_NAME = "Current playlist";
	public static final String CONFIRM_DELETION_TITLE = "Confirm deletion?";
	public static final String CONFIRM_DELETION_INFO = "This action will erase the collection, but not any of the song files. You will still be able to find them in \"All music\" and/or in other collections";
	
	public static final int PREVIOUS_SONGS_TO_ASK = 10;
	public static final int NEXT_SONGS_TO_ASK = 29;
	
}

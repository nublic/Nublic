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
	public static final String CURRENT_PLAYLIST_ID = "CurrentPlaylist";
	public static final String CURRENT_PLAYLIST_NAME = "Current playlist";
	public static final String ALL_MUSIC_NAME = "All music";
	public static final String CONFIRM_DELETION_TITLE = "Confirm deletion?";
	public static final String CONFIRM_DELETION_INFO = "This action will erase the collection, but not any of the song files. You will still be able to find them in \"All music\" and/or in other collections";
	
	public static final int PREVIOUS_SONGS_TO_ASK = 10;
	public static final int NEXT_SONGS_TO_ASK = 24;
	public static final int NEAR_TO_SCREEN = 100; // pixels from screen since we start loading objects, to anticipate user scrolling
	public static final int UPDATE_SAMPLE_MILLISECONDS = 200;
	public static final double LOADING_ERROR_MARGIN = 0.97; // if we have loaded more than this we consider it to be all

	public static final String GRABBER_WIDTH = "36px";
	public static final String BUTTONS_WIDTH = "48px";
	public static final String TRACK_NUMBER_WIDTH = "40px";
	
}

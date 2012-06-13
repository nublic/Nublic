package com.nublic.app.music.client;

import com.google.gwt.core.client.GWT;
import com.nublic.app.music.client.i18n.I18NMessages;
import com.nublic.app.music.client.ui.ButtonType;

public class Constants {
	public static I18NMessages I18N = GWT.create(I18NMessages.class);
	
	public static final String PARAM_COLLECTION = "Collection";
	public static final String PARAM_PLAYLIST = "Playlist";
	public static final String PARAM_ARTIST = "Artist";
	public static final String PARAM_ALBUM = "Album";
	public static final String PARAM_VIEW = "View";
	public static final String VIEW_ARTISTS = "Artists";
	public static final String VIEW_ALBUMS = "Albums";
	public static final String VIEW_SONGS = "Songs";
	public static final String ORDER_ALPHA = "alpha";				// alpha -> alphabetical by song title
	public static final String ORDER_ARTIST_ALPHA = "artist_alpha"; // artist_alpha -> first artist name, then song title
	public static final String ORDER_ARTIST_ALBUM = "artist_album"; // artist_album -> first artist name, then album name, then disc_no, then track
	public static final String ORDER_ALBUM = "album";				// album -> first album name, then disc_no, then track

	public static final String CURRENT_PLAYLIST_ID = "CurrentPlaylist";
	
	public static final int PREVIOUS_SONGS_TO_ASK = 10;
	public static final int NEXT_SONGS_TO_ASK = 24;
	public static final int NEAR_TO_SCREEN = 100; // pixels from screen since we start loading objects, to anticipate user scrolling
	public static final int UPDATE_SAMPLE_MILLISECONDS = 200;
	public static final double LOADING_ERROR_MARGIN = 0.97; // if we have loaded more than this we consider it to be all

	public static final String GRABBER_WIDTH = "36px";
	public static final String BUTTONS_WIDTH = "48px";
	public static final String TRACK_NUMBER_WIDTH = "30px";
	public static final String TABLE_CELL_HEIGHT = "27px";

	public static String tooltip(ButtonType blpt) {
		switch (blpt) {
		case DELETE_COLLECTION:
			return I18N.deleteCollectionTooltip();
		case DELETE_PLAYLIST:
			return I18N.deletePlaylistTooltip();
		case DELETE_PLAYLIST_SONG:
			return I18N.deletePlaylistSongTootip();
		case DELETE_COLLECTION_SONG:
			return I18N.deleteCollectionSongTootip();
		case DELETE_COLLECTION_ALBUM:
			return I18N.deleteCollectionAlbumTootip();
		case DELETE_COLLECTION_ARTIST:
			return I18N.deleteCollectionArtistTootip();
		case EDIT_ALBUM:
			return I18N.editAlbumTooltip();
		case EDIT_ARTIST:
			return I18N.editArtistTooltip();
		case EDIT_SONG:
			return I18N.editSongTooltip();
		case PLAY_ALBUM:
			return I18N.playAlbumTooltip();
		case PLAY_ARTIST:
			return I18N.playArtistTooltip();
		case PLAY_SONG:
			return I18N.playSongTooltip();
		case PLAY_PLAYLIST:
			return I18N.playPlaylistTooltip();
		case PLAY_COLLECTION:
			return I18N.playCollectionTooltip();
		default:
			return "";
		}
	}
	
}

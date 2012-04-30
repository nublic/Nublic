package com.nublic.app.music.client.i18n;

import com.google.gwt.i18n.client.Messages;

public interface I18NMessages extends Messages {
	String windowPretitle();
	
	String confirmDeletionTitle();
	String confirmDeletionInfo();
	
	// Navigation panel titles
	String library();
	String collections();
	String playlists();

	String allMusicName();
	String allAlbums();
	String allSongs();

	String currentPlaylist();

	String newCollection();
	String addCollection();
	String newPlaylist();
	String addPlaylist();

	// Tabs
	String artistView();
	String albumView();
	String songView();
	
	// Errors
	String addCollectionError();
	String addPlaylistError();
	String addToCollectionError();
	String addToPlaylistError();
	String getArtistsError();
	String getAlbumsError();
	String getSongsError();
	String getPlaylistSongsError();
	String getPlaylistListError();
	String getCollectionListError();
	String deletePlaylistError();
	String deleteCollectionError();
	String deletePlaylistSongError();
	String moveSongError();
	String noPluginAvialableError();
}

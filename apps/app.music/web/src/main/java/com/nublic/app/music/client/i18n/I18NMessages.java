package com.nublic.app.music.client.i18n;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;

@DefaultLocale("en")
public interface I18NMessages extends Messages {
	String windowTitleSong(String songTitle);
	String windowTitle();
	
	String confirmDeletionTitle();
	String confirmDeletionInfo();
	
	String deleteTooltip();
	String editTooltip();
	String addAtEndTooltip();
	String playTooltip();
	String deleteCollectionTooltip();
	String deletePlaylistTooltip();
	String deletePlaylistSongTootip();
	String deleteCollectionSongTootip();
	String playArtistTooltip();
	String playAlbumTooltip();
	String playSongTooltip();
	String playPlaylistTooltip();
	String playCollectionTooltip();
	String editArtistTooltip();
	String editAlbumTooltip();
	String editSongTooltip();

	// Navigation panel titles
	String library();
	String collections();
	String playlists();

	String empty();
	String backToCollection();
	String by();

	String allMusic();
//	String allAlbums();
//	String allSongs();

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
	String deleteCollectionSongError();
	String moveSongError();
	String noPluginAvailableError();
}

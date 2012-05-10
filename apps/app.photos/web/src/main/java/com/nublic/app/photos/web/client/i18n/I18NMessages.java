package com.nublic.app.photos.web.client.i18n;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;

@DefaultLocale("en")
public interface I18NMessages extends Messages {
	String library();
	String albums();
	String newAlbum();
	String addAlbum();
	String addNewAlbum();
	String deleteAlbum();
	String deleteAlbumText();
	String deleteThisAlbum();
	String removeSelectedPhotos();
	String allPhotos();
	String allAlbums();
	String unknownAlbum();
	String swichToGrid();
	String takenOn(String date);
	
	// Errors
	String errorLoadingPhoto();
}

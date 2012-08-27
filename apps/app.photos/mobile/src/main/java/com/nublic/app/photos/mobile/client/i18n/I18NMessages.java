package com.nublic.app.photos.mobile.client.i18n;

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
	String nPhotos(@PluralCount int numberOfPhotos);
	String windowTitle();
	String windowTitlePhoto(String s);
	
	// Errors
	String errorLoadingPhoto();
}

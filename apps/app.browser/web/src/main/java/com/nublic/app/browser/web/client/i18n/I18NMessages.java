package com.nublic.app.browser.web.client.i18n;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.safehtml.shared.SafeHtml;

@DefaultLocale("en")
public interface I18NMessages extends Messages {
	// Headers
	String windowTitle(String path);
	String home();
	String nublicFiles();
	String actions();
	
	// Ordering dropdown
	String orderBy();
	String orderName();
	String orderType();
	String orderDate();
	String orderSize();
	
	// Sparkleshare
	String sparkleButton();
	String sparkleFolder();
	SafeHtml sparkleText();
	
	// Actions/tooltips
	String createNewFolder();
	String newFolderName();
	String uploadFile();
	String addFile();
	String paste();
	String pasteN(@PluralCount int n);
	String filter();
	String selectAll();
	String unselectAll();
	String clearClipboard();
	String copy();
	String cut();
	String delete();
	String confirmDeletion();
	String confirmDeletionText();
	String dowloadFolder();
	String movingNElements(@PluralCount int n);
	String copyingNElements(@PluralCount int n);
	String previewDocument();
	String previewImage();
	String previewMusic();
	String previewText();
	String previewVideo();
	String rename();
	String newName();
	String downloadAsZip();
	String downloadFile();
	String uploadingFile(String name);
	
	// Selection info
	String nItems(@PluralCount int n);
	String mFolders(@PluralCount int m);
	String nFilesSize(@PluralCount int n, String size);
	String modifiedDate(String date);
	
	// Errors
	String imageFileNotFound();
	String errorReadingTextFile();
	String errorLoadingTextViewer();
	String noPathToResource();
	String couldNotDeleteFiles();
	String couldNotCreateFolder();
	String couldNotMoveFiles();
	String couldNotCopyFiles();
	String couldNotRename();
	String errorUploading();
}

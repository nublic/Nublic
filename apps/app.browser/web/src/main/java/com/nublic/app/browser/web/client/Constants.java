package com.nublic.app.browser.web.client;

public class Constants {
	public final static int POPUP_MARGIN = 50;
	public final static int POPUP_BOTTOM = 30; // also defined in PopupContent.ui.xml
	public final static int DEFAULT_DEPTH = 2; // Number of levels of the tree requested each time
	public final static int DRAG_START_SENSITIVIY = 10;
	public final static int TIME_TO_OPEN = 1000; // when mouse is over a tree item
	public final static String BROWSER_VIEW = "browser";
	public final static String PATH_PARAMETER = "path";
	public final static String IMAGE_VIEW = "image";
	public final static String IMAGE_TYPE = "png";
	public final static String DOCUMENT_VIEW = "document";
	public final static String DOCUMENT_TYPE = "pdf";
	public final static String MUSIC_VIEW = "music";
	public final static String MUSIC_TYPE = "mp3";
	public final static String VIDEO_VIEW = "video";
	public final static String VIDEO_TYPE = "flv";
	public final static String TEXT_VIEW = "text";
	public final static String TEXT_TYPE = "txt";
	public final static String FOLDER_MIME = "application/x-directory";
	public final static String FOLDER_TYPE = "folder";
	public final static String COMPRESSED_TYPE = "zip";
	public final static String WINDOW_PRETITLE = "Browser - ";
	public final static String WINDOW_HOME_TITLE = "Home";
	
	public final static String KIND_MIRROR = "mirror";
	public final static String KIND_SYNCED = "synced";
	public final static String KIND_MEDIA  = "media";
	public final static String NUBLIC_ONLY = "nublic-only";
	public final static String KIND_MIRROR_FOLDER = "mirrors";
	public final static String KIND_SYNCED_FOLDER = "synced";
	public final static String KIND_MEDIA_FOLDER  = "media";
	
	public final static String getView(String type) {
		String retStr = null;
		if (type.equals(Constants.IMAGE_TYPE)) {
			retStr = IMAGE_VIEW;
		} else if (type.equals(Constants.DOCUMENT_TYPE)) {
			retStr = DOCUMENT_VIEW;
		} else if (type.equals(Constants.MUSIC_TYPE)) {
			retStr = MUSIC_VIEW;
		} else if (type.equals(Constants.VIDEO_TYPE)) {
			retStr = VIDEO_VIEW;
		} else if (type.equals(Constants.FOLDER_TYPE)) {
			retStr = BROWSER_VIEW;
		} else if (type.equals(Constants.TEXT_TYPE)) {
			retStr = TEXT_VIEW;
		}
		return retStr;
	}
}

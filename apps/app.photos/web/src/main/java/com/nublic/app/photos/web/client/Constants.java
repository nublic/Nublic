package com.nublic.app.photos.web.client;

import com.google.gwt.core.client.GWT;
import com.nublic.app.photos.web.client.i18n.I18NMessages;

public class Constants {
	public static I18NMessages I18N = GWT.create(I18NMessages.class);
	
	public static int ALL_PHOTOS = -1;
	public static int ALL_ALBUMS = -2;
}

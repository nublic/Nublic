package com.nublic.app.manager.web.client;

import com.google.gwt.core.client.GWT;
import com.nublic.app.manager.web.client.i18n.I18NMessages;

public class Constants {
	public static I18NMessages I18N = GWT.create(I18NMessages.class);
	
	public static String getTitle(String app) {
		String trimmed = app.trim();
		if (trimmed == null || trimmed.isEmpty()) {
			return Constants.I18N.titleWithoutApp();
		} else {
			return Constants.I18N.titleWithApp(trimmed);
		}
	}
	
	public static String settingsAppName = "settings";
	public static String welcomeAppName  = "welcome";
}

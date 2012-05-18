package com.nublic.app.manager.web.client.i18n;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;

@DefaultLocale("en")
public interface I18NMessages extends Messages {
	String titleWithoutApp();
	String titleWithApp(String app);
	String settings();
	
	// Errors
	String errorLoadingAppList();
}

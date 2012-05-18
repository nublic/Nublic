package com.nublic.app.manager.welcome.client.i18n;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.safehtml.shared.SafeHtml;

@DefaultLocale("en")
public interface I18NMessages extends Messages {
	String title();
	String greeting(String user);
	
	String welcomeToNublic();
	SafeHtml thanksForTesting();
	String yourApps();
	
	// Errors
	String errorLoadingAppList();
}

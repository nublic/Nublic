package com.nublic.app.market.web.client.i18n;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;

@DefaultLocale("en")
public interface I18NMessages extends Messages {
	String developer();

	String installed();
	String installedHover();
	String installing();
	String notInstalled();
	String notInstalledHover();
	String error();

	// Errors
	String errorGetAppList();
	String errorCouldNotInstall();
	String errorCouldNotUninstall();


}

package com.nublic.app.manager.settings.client;

import com.google.gwt.core.client.GWT;
import com.nublic.app.manager.settings.client.i18n.I18NMessages;

public class Constants {
	public static I18NMessages I18N = GWT.create(I18NMessages.class);
	public static com.nublic.util.i18n.I18NMessages UTIL_I18N = GWT.create(com.nublic.util.i18n.I18NMessages.class);
}

package com.nublic.app.init.client;

import com.google.gwt.core.client.GWT;
import com.nublic.app.init.client.i18n.I18NMessages;

public class Constants {
	public static I18NMessages I18N = GWT.create(I18NMessages.class);
	
	public static final String PARAM_PAGE = "Page";
	public static final String VALUE_USERS = "Users";
	
}

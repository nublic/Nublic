package com.nublic.app.market.web.client;

import com.google.gwt.core.client.GWT;
import com.nublic.app.market.web.client.i18n.I18NMessages;

public class Constants {
	public static I18NMessages I18N = GWT.create(I18NMessages.class);
	
	public static final String PARAM_APP = "app";
	public static final String INSTALLED_STYLE = "btn-success disabled";
	public static final String INSTALLED_HOVER_STYLE = "btn-danger";
	public static final String INSTALLING_STYLE = "btn-warning disabled";
	public static final String NOT_INSTALLED_STYLE = "btn-info disabled";
	public static final String NOT_INSTALLED_HOVER_STYLE = "btn-primary";
	public static final String ERROR_STYLE = "btn-danger disabled";

	public static final int POLLING_TIME = 3500;

}

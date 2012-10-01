package com.nublic.app.init.client.i18n;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;

@DefaultLocale("en")
public interface I18NMessages extends Messages {

	String userStep();
	String masterUserStep();
	String netConfigStep();
	String nameStep();
//	String nSongs(@PluralCount int n);
}

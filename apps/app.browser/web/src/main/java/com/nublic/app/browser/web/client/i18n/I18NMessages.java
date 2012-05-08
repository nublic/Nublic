package com.nublic.app.browser.web.client.i18n;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.safehtml.shared.SafeHtml;

public interface I18NMessages extends Messages {
	// Headers
	String windowTitle(String path);
	String home();
	String nublicFiles();
	String actions();
	
	// Ordering dropdown
	String orderBy();
	String orderName();
	String orderType();
	String orderDate();
	String orderSize();
	
	// Sparkleshare
	String sparkleButton();
	String sparkleFolder();
	SafeHtml sparkleText();
	
	// Actions/tooltips
	String createNewFolder();
	String addFile();
	String paste();
	String filter();
	String selectAll();
}

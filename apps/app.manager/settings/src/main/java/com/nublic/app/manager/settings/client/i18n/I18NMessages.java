package com.nublic.app.manager.settings.client.i18n;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;

@DefaultLocale("en")
public interface I18NMessages extends Messages {
	String title();
	String user();
	String workFolders();
	
	// Personal config
	String shownName();
	String username();
	String systemName();
	String clickToEdit();
	String changePass();
	String couldntChangePass();
	String oldPass();
	String newPass();
	String confirmPass();
	String passChanged();
	
	// Work folders
	String newWorkFolderName();
	String confirmRemoval(String folder);
	String changeWorkFolderName();
	String browse();
	
	// Errors
	String errorNewWorkFolder();
	String errorRemovalWorkFolder();
	String errorChangeNameWorkFolder();
}

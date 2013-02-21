package com.nublic.app.manager.settings.client.i18n;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;

@DefaultLocale("en")
public interface I18NMessages extends Messages {
	String title();
	String personalConfig();
	String workFolders();
	String privacy();
	String system();
	String users();
	
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
	
	// Users
	String userList();
	String createUser();
	String deleteUser();
	String enterMasterPass1();
	String enterMasterPass2();
	String enterMasterPass3();
	
	// Errors
	String errorNewWorkFolder();
	String errorRemovalWorkFolder();
	String errorChangeNameWorkFolder();
}

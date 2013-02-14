package com.nublic.util.i18n;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;

@DefaultLocale("en")
public interface I18NMessages extends Messages {
	// Widgets
	String cancel();
	String no();
	String custom();
	String add();
	String delete();
	String upload();
	String yes();
	String ok();
	String changeName();
	
	String error();
	
	// Users
	String userNameInvalid();
	String userNameNotAvailable();
	String allFieldsError();
	String userName();
	String userNameHelp();
	String systemUserName();
	String systemUserNameHelp();
	String password();
	String passwordVerification();
	String createUser();
	String shortPassword();
	String noCoincidentPassword();
	String couldNotCreateUser();
}

package com.nublic.app.init.client.i18n;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.safehtml.shared.SafeHtml;

@DefaultLocale("en")
public interface I18NMessages extends Messages {

	// Step header
	String users();
	String masterPassword();
	String networkConfiguration();
	String nameIt();

	// Common stuff
	String title();
	String previousStep();
	String nextStep();
	String completeFirst();

	// Welcome
	String welcomeTitle();
	String welcomeText();
	String welcomeInfo();

	// Users
	String usersText();
	String userName();
	String userNameHelp();
	String systemUserName();
	String systemUserNameHelp();
	String password();
	String passwordVerification();
	String createUser();
	String userNameNotAvailable();
	String userNameInvalid();
	String shortPassword();
	String noCoincidentPassword();
	String allFieldsError();
	String userCreated();
	String couldNotCreateUser();

	// Master
	SafeHtml attentionCopyPassword();
	String iHaveCopiedIt();

	// Network
	String networkText();
	String innerConfig();
	String outsideAccess();
	String urlAvailability();
	String autoConfig();

	// Name
	SafeHtml infoNublicName();
	String nublicName();
	String invalidNublicName();
	String nameText();

	// Finished
	String finishedTitle();
	String finishedText();
	String whatsNext();
	String howSynchronize();
	String homeConnect();
	String access();
//	String nSongs(@PluralCount int n);
}

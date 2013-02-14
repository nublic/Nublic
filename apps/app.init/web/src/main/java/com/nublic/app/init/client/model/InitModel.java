package com.nublic.app.init.client.model;

import com.google.gwt.http.client.RequestBuilder;
import com.nublic.app.init.client.model.handlers.CheckNublicNameHandler;
import com.nublic.app.init.client.model.handlers.PasswordHandler;
import com.nublic.app.init.client.model.handlers.UserListHandler;
import com.nublic.app.init.client.model.messages.CheckNublicNameMessage;
import com.nublic.app.init.client.model.messages.PasswordMessage;
import com.nublic.app.init.client.model.messages.UserListMessage;
import com.nublic.util.messages.SequenceHelper;

public class InitModel {
	public static InitModel INSTANCE;
	
	public static InitModel create() {
		if (INSTANCE == null) {
			INSTANCE = new InitModel();
		}
		return INSTANCE;
	}

	public InitModel() {

		sendInitialMessages();
	}
	
	private void sendInitialMessages() {
//		TagsMessage tm = new TagsMessage();
//		SequenceHelper.sendJustOne(tm, RequestBuilder.GET);
	}
	
	public void getUserList(UserListHandler ulh) {
		UserListMessage ulm = new UserListMessage(ulh);
		SequenceHelper.sendJustOne(ulm, RequestBuilder.GET);
	}
	
	public void getMasterPassword(PasswordHandler ph) {
		PasswordMessage pm = new PasswordMessage(ph);
		SequenceHelper.sendJustOne(pm, RequestBuilder.GET);
	}

	public void checkNublicNameAvailability(String name, CheckNublicNameHandler cnnh) {
		CheckNublicNameMessage cnnm = new CheckNublicNameMessage(name, cnnh);
		SequenceHelper.sendJustOne(cnnm, RequestBuilder.GET);
	}

}

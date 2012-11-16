package com.nublic.app.init.client.model;

import com.google.gwt.http.client.RequestBuilder;
import com.nublic.app.init.client.Constants;
import com.nublic.app.init.client.model.handlers.AddUserHandler;
import com.nublic.app.init.client.model.handlers.CheckNublicNameHandler;
import com.nublic.app.init.client.model.handlers.CheckUserHandler;
import com.nublic.app.init.client.model.handlers.PasswordHandler;
import com.nublic.app.init.client.model.handlers.UserListHandler;
import com.nublic.app.init.client.model.messages.AddUserMessage;
import com.nublic.app.init.client.model.messages.CheckNublicNameMessage;
import com.nublic.app.init.client.model.messages.CheckUserMessage;
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
	
	public static String getSystemName(String realName) {	
		String temp = realName.toLowerCase()
				.replaceAll("[^a-zA-Z0-9_]", "")
				.replaceAll("^[0-9]*","");
		return temp.substring(0,
				temp.length() > Constants.MAX_USERNAME_LENGTH ? Constants.MAX_USERNAME_LENGTH : temp.length());

//		System.out.println("a:");
//		System.out.println(realName);
//		System.out.println(realName.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")); // This seems to do nothing
//		System.out.println(realName.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase());
//		System.out.println(realName.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase().replaceAll("[^a-zA-Z0-9_]", ""));
//		return realName.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase().replaceAll("[^a-zA-Z0-9_]", "");
//		return Normalizer.normalize(realName, Form.NFKD).replaceAll("[^a-zA-Z0-9_]", "");
	}
	
	public void getUserList(UserListHandler ulh) {
		UserListMessage ulm = new UserListMessage(ulh);
		SequenceHelper.sendJustOne(ulm, RequestBuilder.GET);
	}
	
	public void checkUserAvailability(String name, CheckUserHandler cuh) {
		CheckUserMessage cum = new CheckUserMessage(name, cuh);
		SequenceHelper.sendJustOne(cum, RequestBuilder.GET);
	}
	
	public void addUser(String name, String password, AddUserHandler auh) {
		AddUserMessage aum = new AddUserMessage(name, password, auh);
		SequenceHelper.sendJustOne(aum, RequestBuilder.PUT);
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

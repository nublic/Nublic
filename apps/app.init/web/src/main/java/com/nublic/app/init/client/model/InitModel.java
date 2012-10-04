package com.nublic.app.init.client.model;

import com.google.gwt.http.client.RequestBuilder;
import com.nublic.app.init.client.model.handlers.AddUserHandler;
import com.nublic.app.init.client.model.handlers.CheckUserHandler;
import com.nublic.app.init.client.model.messages.AddUserMessage;
import com.nublic.app.init.client.model.messages.CheckUserMessage;
import com.nublic.util.messages.SequenceHelper;

public class InitModel {

	public InitModel() {

		sendInitialMessages();
	}
	
	private void sendInitialMessages() {
//		TagsMessage tm = new TagsMessage();
//		SequenceHelper.sendJustOne(tm, RequestBuilder.GET);
	}
	
	public void checkUserAvailability(String name, CheckUserHandler cuh) {
		CheckUserMessage cum = new CheckUserMessage(name, cuh);
		SequenceHelper.sendJustOne(cum, RequestBuilder.GET);
	}
	
	public void addUser(String name, String password, AddUserHandler auh) {
		AddUserMessage aum = new AddUserMessage(name, password, auh);
		SequenceHelper.sendJustOne(aum, RequestBuilder.POST);
	}
}

package com.nublic.app.init.client.model;

import com.nublic.app.init.client.model.js.JSUser;

public class User {
	String userName;
	String shownName;
	
	public User(JSUser jsUser) {
		this(jsUser.getUserName(), jsUser.getShownName());
	}

	public User(String userName, String shownName) {
		this.userName = userName;
		this.shownName = shownName;
	}

	public String getUserName() {
		return userName;
	}
	
	public String getShownName() {
		return shownName;
	}
}

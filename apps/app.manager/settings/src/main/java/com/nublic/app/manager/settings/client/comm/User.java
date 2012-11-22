package com.nublic.app.manager.settings.client.comm;

public class User {
	String shownName;
	String systemName;
	
	public User(JSUser jsUser) {
		shownName = jsUser.getShownName();
		systemName = jsUser.getUserName();
	}

	public String getShownName() {
		return shownName;
	}

	public String getSystemName() {
		return systemName;
	}
	
}

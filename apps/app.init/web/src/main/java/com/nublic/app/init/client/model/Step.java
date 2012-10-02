package com.nublic.app.init.client.model;

import com.google.gwt.resources.client.ImageResource;
import com.nublic.app.init.client.Constants;
import com.nublic.app.init.client.Resources;
import com.nublic.app.init.client.ui.CentralPanel;
import com.nublic.app.init.client.ui.UsersPage;
import com.nublic.app.init.client.ui.WelcomePage;

public enum Step {
	WELCOME("",
			null,
			new WelcomePage()),
	USERS(Constants.I18N.userStep(),
			Resources.INSTANCE.users(),
			new UsersPage()),
	MASTER_USER(Constants.I18N.masterUserStep(),
			Resources.INSTANCE.master(),
			new WelcomePage()),
	NET_CONFIG(Constants.I18N.netConfigStep(),
			Resources.INSTANCE.network(),
			new WelcomePage()),
	NAME(Constants.I18N.nameStep(),
			Resources.INSTANCE.name(),
			new WelcomePage()),
	FINISHED("",
			null,
			new WelcomePage());
	
	String name;
	ImageResource image;
	CentralPanel uiWidget;
	
	private Step(String name, ImageResource image, CentralPanel uiWidget) {
		this.name = name;
		this.image = image;
		this.uiWidget = uiWidget;
	}
	
	public String getName() {
		return name;
	}
	
	public ImageResource getImage() {
		return image;
	}

	public CentralPanel getUiWidget() {
		return uiWidget;
	}
	
	public static Step parseString(String s) {
		if (s == null) {
			return null;
		} else if (Constants.VALUE_WELCOME.compareTo(s) == 0) {
			return WELCOME;
		} else if (Constants.VALUE_USERS.compareTo(s) == 0) {
			return USERS;
		} else if (Constants.VALUE_MASTER_USER.compareTo(s) == 0) {
			return MASTER_USER;
		}
		return null;
	}
}

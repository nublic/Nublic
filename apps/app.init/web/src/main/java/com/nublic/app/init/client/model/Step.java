package com.nublic.app.init.client.model;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.nublic.app.init.client.Constants;
import com.nublic.app.init.client.Resources;
import com.nublic.app.init.client.ui.CentralPanel;
import com.nublic.app.init.client.ui.master.MasterPage;
import com.nublic.app.init.client.ui.users.UsersPage;
import com.nublic.app.init.client.ui.welcome.WelcomePage;

public enum Step {
//	WELCOME("",
//			null,
//			new WelcomePage()),
//	USERS(Constants.I18N.userStep(),
//			Resources.INSTANCE.users(),
//			new UsersPage()),
//	MASTER_USER(Constants.I18N.masterUserStep(),
//			Resources.INSTANCE.master(),
//			new MasterPage()),
//	NET_CONFIG(Constants.I18N.netConfigStep(),
//			Resources.INSTANCE.network(),
//			new WelcomePage()),
//	NAME(Constants.I18N.nameStep(),
//			Resources.INSTANCE.name(),
//			new WelcomePage()),
//	FINISHED("",
//			null,
//			new WelcomePage());
	WELCOME("",
			null,
			WelcomePage.class),
	USERS(Constants.I18N.userStep(),
			Resources.INSTANCE.users(),
			UsersPage.class),
	MASTER_USER(Constants.I18N.masterUserStep(),
			Resources.INSTANCE.master(),
			MasterPage.class),
	NET_CONFIG(Constants.I18N.netConfigStep(),
			Resources.INSTANCE.network(),
			WelcomePage.class),
	NAME(Constants.I18N.nameStep(),
			Resources.INSTANCE.name(),
			WelcomePage.class),
	FINISHED("",
			null,
			WelcomePage.class);
	
	String name;
	ImageResource image;
	Class<? extends CentralPanel> widgetClass;
	CentralPanel uiWidget = null;
	
	private Step(String name, ImageResource image, CentralPanel uiWidget) {
		this.name = name;
		this.image = image;
		this.uiWidget = uiWidget;
	}
	
	private Step(String name, ImageResource image, Class<? extends CentralPanel> widgetClass) {
		this.name = name;
		this.image = image;
		this.widgetClass = widgetClass;
	}
	
	public String getName() {
		return name;
	}
	
	public ImageResource getImage() {
		return image;
	}

	public CentralPanel getUiWidget() {
		if (uiWidget == null) {
			try {
				uiWidget = GWT.create(widgetClass);
			} catch (Exception e) {
				// nothing
			}
		}
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
		} else if (Constants.VALUE_NET_CONFIG.compareTo(s) == 0) {
			return NET_CONFIG;
		}
		return null;
	}
}

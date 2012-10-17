package com.nublic.app.init.client.model;

import com.google.gwt.resources.client.ImageResource;
import com.nublic.app.init.client.Constants;
import com.nublic.app.init.client.Resources;
import com.nublic.app.init.client.ui.CentralPanel;
import com.nublic.app.init.client.ui.finished.FinishedPage;
import com.nublic.app.init.client.ui.master.MasterPage;
import com.nublic.app.init.client.ui.name.NamePage;
import com.nublic.app.init.client.ui.network.NetworkPage;
import com.nublic.app.init.client.ui.users.UsersPage;
import com.nublic.app.init.client.ui.welcome.WelcomePage;

public enum Step {
	WELCOME("",
			null,
			new WelcomePage()),
	USERS(Constants.I18N.userStep(),
			Resources.INSTANCE.users(),
			new UsersPage()),
	MASTER_USER(Constants.I18N.masterUserStep(),
			Resources.INSTANCE.master(),
			new MasterPage()),
	NET_CONFIG(Constants.I18N.netConfigStep(),
			Resources.INSTANCE.network(),
			new NetworkPage()),
	NAME(Constants.I18N.nameStep(),
			Resources.INSTANCE.name(),
			new NamePage()),
	FINISHED("",
			null,
			new FinishedPage());
//	WELCOME("",
//			null,
//			WelcomePage.class),
//	USERS(Constants.I18N.userStep(),
//			Resources.INSTANCE.users(),
//			UsersPage.class),
//	MASTER_USER(Constants.I18N.masterUserStep(),
//			Resources.INSTANCE.master(),
//			MasterPage.class),
//	NET_CONFIG(Constants.I18N.netConfigStep(),
//			Resources.INSTANCE.network(),
//			NetworkPage.class),
//	NAME(Constants.I18N.nameStep(),
//			Resources.INSTANCE.name(),
//			NamePage.class),
//	FINISHED("",
//			null,
//			FinishedPage.class);
	
	String name;
	ImageResource image;
	Class<? extends CentralPanel> widgetClass;
	CentralPanel uiWidget = null;
	
	private Step(String name, ImageResource image, CentralPanel uiWidget) {
		this.name = name;
		this.image = image;
		this.uiWidget = uiWidget;
	}
	
//	private Step(String name, ImageResource image, Class<? extends CentralPanel> widgetClass) {
//		this.name = name;
//		this.image = image;
//		this.widgetClass = widgetClass;
//	}
	
	public String getName() {
		return name;
	}
	
	public ImageResource getImage() {
		return image;
	}

	public CentralPanel getUiWidget() {
//		if (uiWidget == null) {
//			try {
////				uiWidget = widgetClass.newInstance(); 
//				uiWidget = GWT.create(widgetClass);
//			} catch (Exception e) {
//				// nothing
//			}
//		}
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
		} else if (Constants.VALUE_NAME.compareTo(s) == 0) {
			return NAME;
		} else if (Constants.VALUE_FINISHED.compareTo(s) == 0) {
			return FINISHED;
		}
		return null;
	}
}

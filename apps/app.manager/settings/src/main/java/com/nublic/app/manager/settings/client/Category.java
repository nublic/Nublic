package com.nublic.app.manager.settings.client;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.manager.settings.client.ui.PersonalPage;
import com.nublic.app.manager.settings.client.ui.UsersPage;

public enum Category {
	PERSONAL(Constants.VALUE_PERSONAL, new PersonalPage()),
	WORK_FOLDERS(Constants.VALUE_WORK_FOLDERS, new HTMLPanel("a")),
	PRIVACY(Constants.VALUE_PRIVACY, new HTMLPanel("b")),
	SYSTEM(Constants.VALUE_SYSTEM, new HTMLPanel("c")),
	USERS(Constants.VALUE_USERS, new UsersPage());

	private String str;
	private Widget centralWidget;
	
	private Category(String s, Widget w) {
		str = s;
		centralWidget = w;
	}
	
	@Override
	public String toString() {
		return str;
	}

	public static Category parse(String catString) {
		if (catString == null) {
			return null;
		}
		for (Category c : Category.values()) {
			if (c.toString().compareTo(catString) == 0) {
				return c;
			}
		}
		return null;
	}

	public Widget getCentralWidget() {
		return centralWidget;
	}
}

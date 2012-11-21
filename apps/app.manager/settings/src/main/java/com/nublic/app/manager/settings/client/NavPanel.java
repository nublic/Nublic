package com.nublic.app.manager.settings.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class NavPanel extends Composite {
	private static NavPanelUiBinder uiBinder = GWT.create(NavPanelUiBinder.class);
	interface NavPanelUiBinder extends UiBinder<Widget, NavPanel> {	}
	
	@UiField NavLink personal;
	@UiField NavLink workFolders;
	@UiField NavLink privacy;
	@UiField NavLink system;
	@UiField NavLink users;

	public NavPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void select(Category c) {
		personal.select(c == Category.PERSONAL);
		workFolders.select(c == Category.WORK_FOLDERS);
		privacy.select(c == Category.PRIVACY);
		system.select(c == Category.SYSTEM);
		users.select(c == Category.USERS);
	}

}

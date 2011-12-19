package com.nublic.app.manager.web.settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.manager.web.client.ManagerUi;

public class SettingsPage extends Composite {

	private static SettingsPageUiBinder uiBinder = GWT.create(SettingsPageUiBinder.class);
	
	ManagerUi theUi;

	interface SettingsPageUiBinder extends UiBinder<Widget, SettingsPage> {
	}

	public SettingsPage(ManagerUi ui) {
		initWidget(uiBinder.createAndBindUi(this));
		this.theUi = ui;
	}

}

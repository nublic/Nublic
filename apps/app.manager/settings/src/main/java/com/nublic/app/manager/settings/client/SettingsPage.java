package com.nublic.app.manager.settings.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class SettingsPage extends Composite {

	private static SettingsPageUiBinder uiBinder = GWT.create(SettingsPageUiBinder.class);

	interface SettingsPageUiBinder extends UiBinder<Widget, SettingsPage> {
	}

	public SettingsPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}

package com.nublic.app.manager.settings.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.util.widgets.EditableLabel;

public class UserSettingsTab extends Composite {

	private static UserSettingsTabUiBinder uiBinder = GWT.create(UserSettingsTabUiBinder.class);

	interface UserSettingsTabUiBinder extends UiBinder<Widget, UserSettingsTab> {
	}
	
	@UiField EditableLabel name;

	public UserSettingsTab() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}

package com.nublic.app.manager.settings.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ExistingUser extends Composite {
	private static ExistingUserUiBinder uiBinder = GWT.create(ExistingUserUiBinder.class);
	interface ExistingUserUiBinder extends UiBinder<Widget, ExistingUser> {}

	@UiField Label shownLabel;
	@UiField Label systemLabel;
	
	public ExistingUser(String systemName, String shownName) {
		initWidget(uiBinder.createAndBindUi(this));
		
		shownLabel.setText(shownName);
		systemLabel.setText(systemName);
	}

}

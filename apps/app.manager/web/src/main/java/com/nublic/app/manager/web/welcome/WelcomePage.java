package com.nublic.app.manager.web.welcome;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;

public class WelcomePage extends Composite {

	private static WelcomePageUiBinder uiBinder = GWT.create(WelcomePageUiBinder.class);
	@UiField Grid appGrid;
	@UiField Label welcomeLabel;

	interface WelcomePageUiBinder extends UiBinder<Widget, WelcomePage> {
	}

	public WelcomePage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}

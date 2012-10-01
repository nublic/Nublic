package com.nublic.app.init.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class WelcomePage extends Composite {
	private static WelcomePageUiBinder uiBinder = GWT.create(WelcomePageUiBinder.class);
	interface WelcomePageUiBinder extends UiBinder<Widget, WelcomePage> {}

	public WelcomePage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}

package com.nublic.app.init.client.ui;

import java.util.EnumSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.init.client.Constants;
import com.nublic.app.init.client.model.Step;

public class WelcomePage extends CentralPanel {
	private static WelcomePageUiBinder uiBinder = GWT.create(WelcomePageUiBinder.class);
	interface WelcomePageUiBinder extends UiBinder<Widget, WelcomePage> {}
	
	@UiField Hyperlink nextLink;
	
	public WelcomePage() {
		initWidget(uiBinder.createAndBindUi(this));
		
		nextLink.setTargetHistoryToken(Constants.PARAM_PAGE + "=" + Constants.VALUE_USERS);
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public EnumSet<Step> getNextAllowed() {
		return EnumSet.of(Step.USERS);
	}

}

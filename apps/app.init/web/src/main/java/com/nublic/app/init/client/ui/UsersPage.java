package com.nublic.app.init.client.ui;

import java.util.EnumSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.init.client.Constants;
import com.nublic.app.init.client.model.Step;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Hyperlink;

public class UsersPage extends CentralPanel {
	private static UsersPageUiBinder uiBinder = GWT.create(UsersPageUiBinder.class);
	interface UsersPageUiBinder extends UiBinder<Widget, UsersPage> {}

	@UiField Hyperlink nextLink;

	public UsersPage() {
		initWidget(uiBinder.createAndBindUi(this));
		
		nextLink.setTargetHistoryToken(Constants.PARAM_PAGE + "=" + Constants.VALUE_MASTER_USER);
	}

	@Override
	public boolean isReady() {
		return false;
	}
	
	@Override
	public EnumSet<Step> getNextAllowed() {
		return EnumSet.of(Step.WELCOME, Step.MASTER_USER);
	}

}

package com.nublic.app.init.client.ui.users;

import java.util.EnumSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.init.client.Constants;
import com.nublic.app.init.client.model.Step;
import com.nublic.app.init.client.ui.CentralPanel;
import com.google.gwt.uibinder.client.UiField;

public class UsersPage extends CentralPanel {
	private static UsersPageUiBinder uiBinder = GWT.create(UsersPageUiBinder.class);
	interface UsersPageUiBinder extends UiBinder<Widget, UsersPage> {}

	@UiField InlineHyperlink previousLink;
	@UiField InlineHyperlink nextLink;
	@UiField HTMLPanel centralPanel;

	public UsersPage() {
		initWidget(uiBinder.createAndBindUi(this));

		previousLink.setTargetHistoryToken(Constants.PARAM_PAGE + "=" + Constants.VALUE_WELCOME);
		nextLink.setTargetHistoryToken(Constants.PARAM_PAGE + "=" + Constants.VALUE_MASTER_USER);
		
		// Get created users from model
		

	}
	
	public void addCompletedUser(String name) {
		centralPanel.add(new CompletedUserWidget(name));
	}

	@Override
	public boolean canChangeTo(Step s) {
		return EnumSet.of(Step.WELCOME, Step.USERS).contains(s) ||
				(s == Step.MASTER_USER && centralPanel.getWidgetCount() != 0);
	}

}

package com.nublic.app.init.client.ui.users;

import java.util.EnumSet;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.init.client.Constants;
import com.nublic.app.init.client.model.InitModel;
import com.nublic.app.init.client.model.Step;
import com.nublic.app.init.client.model.User;
import com.nublic.app.init.client.model.handlers.UserListHandler;
import com.nublic.app.init.client.ui.CentralPanel;
import com.nublic.app.init.client.ui.FooterPagination;
import com.google.gwt.uibinder.client.UiField;

public class UsersPage extends CentralPanel {
	private static UsersPageUiBinder uiBinder = GWT.create(UsersPageUiBinder.class);
	interface UsersPageUiBinder extends UiBinder<Widget, UsersPage> {}

	@UiField FooterPagination footer;
	@UiField HTMLPanel centralPanel;

	public UsersPage() {
		initWidget(uiBinder.createAndBindUi(this));

		footer.setLinks(Constants.PARAM_PAGE + "=" + Constants.VALUE_WELCOME,
				Constants.PARAM_PAGE + "=" + Constants.VALUE_MASTER_USER);
		
		// Get created users from model
		InitModel.INSTANCE.getUserList(new UserListHandler() {
			@Override
			public void onUserList(List<User> userList) {
				for (User u : userList) {
					addCompletedUser(u.getUserName(), u.getShownName());
				}
			}
		});
	}
	
	public void addCompletedUser(String systemName, String shownName) {
		footer.highlightNext();
		centralPanel.add(new CompletedUserWidget(systemName, shownName));
	}

	@Override
	public boolean canChangeTo(Step s) {
		return EnumSet.of(Step.WELCOME, Step.USERS).contains(s) ||
				(s == Step.MASTER_USER && centralPanel.getWidgetCount() != 0);
	}

}

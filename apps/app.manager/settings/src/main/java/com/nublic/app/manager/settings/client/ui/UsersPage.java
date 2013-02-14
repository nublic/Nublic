package com.nublic.app.manager.settings.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.util.widgets.AddUserHandler;
import com.nublic.util.widgets.UserWidget;

public class UsersPage extends Composite {
	private static UsersPageUiBinder uiBinder = GWT.create(UsersPageUiBinder.class);
	interface UsersPageUiBinder extends UiBinder<Widget, UsersPage> { }

	@UiField UserWidget createUser;
	
	public UsersPage() {
		initWidget(uiBinder.createAndBindUi(this));
		
		createUser.setFeedbackHandler(new AddUserHandler() {
			@Override
			public void onUserAdded(String systemName, String shownName) {
				// nothing yet
			}
		});
	}

}

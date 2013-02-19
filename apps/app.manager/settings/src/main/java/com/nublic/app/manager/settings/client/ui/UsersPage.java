package com.nublic.app.manager.settings.client.ui;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.manager.settings.client.Constants;
import com.nublic.util.users.AddUserHandler;
import com.nublic.util.users.User;
import com.nublic.util.users.UserListHandler;
import com.nublic.util.users.UserUtils;
import com.nublic.util.users.UserWidget;

public class UsersPage extends Composite {
	private static UsersPageUiBinder uiBinder = GWT.create(UsersPageUiBinder.class);
	interface UsersPageUiBinder extends UiBinder<Widget, UsersPage> { }

	@UiField UserWidget createUser;
	@UiField VerticalPanel existingPanel;
	@UiField Grid existingGrid;
	
	int rowCount = 0;
	
	public UsersPage() {
		initWidget(uiBinder.createAndBindUi(this));

		createUser.setFeedbackHandler(new AddUserHandler() {
			@Override
			public void onUserAdded(String systemName, String shownName) {
				addUserToList(systemName, shownName);
			}
		});
		
		// Get created users from model
		UserUtils.getUserList(new UserListHandler() {
			@Override
			public void onUserList(List<User> userList) {
				for (User u : userList) {
					addUserToList(u.getUserName(), u.getShownName());
				}
			}
		});
	}


	private void addUserToList(String userName, String shownName) {
		existingPanel.add(new ExistingUser(userName, shownName));
		
		
		existingGrid.resize(rowCount +1, 4);
		existingGrid.getColumnFormatter().setWidth(0, Constants.LEFT_GRID_MARGIN);
		existingGrid.getCellFormatter().setHeight(rowCount, 0, Constants.TABLE_CELL_HEIGHT);
		existingGrid.setWidget(rowCount, 1, new Label(shownName));
		existingGrid.setWidget(rowCount, 2, new Label(userName));
		existingGrid.setWidget(rowCount, 3, new Button("Delete User"));

		rowCount++;
	}

}

package com.nublic.app.manager.settings.client.ui;

import java.util.EnumSet;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.manager.settings.client.Constants;
import com.nublic.app.manager.settings.client.DeleteUserHandler;
import com.nublic.app.manager.settings.client.DeleteUserMessage;
import com.nublic.app.manager.settings.client.Resources;
import com.nublic.util.messages.SequenceHelper;
import com.nublic.util.users.AddUserHandler;
import com.nublic.util.users.User;
import com.nublic.util.users.UserListHandler;
import com.nublic.util.users.UserUtils;
import com.nublic.util.users.UserWidget;
import com.nublic.util.widgets.Popup;
import com.nublic.util.widgets.PopupButton;
import com.nublic.util.widgets.PopupButtonHandler;

public class UsersPage extends Composite {
	private static UsersPageUiBinder uiBinder = GWT.create(UsersPageUiBinder.class);
	interface UsersPageUiBinder extends UiBinder<Widget, UsersPage> { }

	// CSS Styles defined in the .xml file
	interface UserStyle extends CssResource {
		String gray();
		String spacing();
		String bold();
	}

	private class MyDeleteHandler implements ClickHandler {
		String systemName;
		
		public MyDeleteHandler(String systemName) {
			this.systemName = systemName;
		}
		
		@Override
		public void onClick(ClickEvent e) {
			EnumSet<PopupButton> buttonSet = EnumSet.of(PopupButton.DELETE, PopupButton.CANCEL);
			final MasterPassWidget mpw = new MasterPassWidget();
			final Popup p = new Popup("Delete user", buttonSet, mpw);
			PopupButtonHandler closeHandler = new PopupButtonHandler() {
				@Override
				public void onClicked(PopupButton button, ClickEvent event) {
					p.hide();
				}
			};
			PopupButtonHandler deleteHandler = new PopupButtonHandler() {
				@Override
				public void onClicked(PopupButton button, ClickEvent event) {
					DeleteUserMessage dum = new DeleteUserMessage(systemName, mpw.getPassword(), new DeleteUserHandler() {
						@Override
						public void onUserDeleted(boolean success) {
							if (success) {
								// remove from interface list
							}
							p.hide();
						}
					});
					SequenceHelper.sendJustOne(dum, RequestBuilder.DELETE);
				}
			};
			p.addButtonHandler(PopupButton.CANCEL, closeHandler);
			p.addButtonHandler(PopupButton.CLOSE, closeHandler);
			p.addButtonHandler(PopupButton.DELETE, deleteHandler);
			p.center();
		}
	}

	@UiField UserStyle style;
	@UiField UserWidget createUser;
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


	private void addUserToList(String systemName, String shownName) {
		existingGrid.resize(rowCount +1, 5);
		existingGrid.getColumnFormatter().setWidth(0, Constants.LEFT_GRID_MARGIN);
		existingGrid.getCellFormatter().setHeight(rowCount, 0, Constants.TABLE_CELL_HEIGHT);
		
		Image person = new Image(Resources.INSTANCE.person());
		person.addStyleName(style.spacing());
		existingGrid.setWidget(rowCount, 1, person);
		
		Label shownLabel = new Label(shownName);
		shownLabel.addStyleName(style.bold());
		shownLabel.addStyleName(style.spacing());
		existingGrid.setWidget(rowCount, 2, shownLabel);
		
		Label systemLabel = new Label(systemName);
		systemLabel.addStyleName(style.gray());
		systemLabel.addStyleName(style.spacing());
		existingGrid.setWidget(rowCount, 3, systemLabel);
		
		Button b = new Button("Delete User");
		b.addStyleName("btn btn-danger");
		b.addClickHandler(new MyDeleteHandler(systemName));
		existingGrid.setWidget(rowCount, 4, b);

		rowCount++;
	}

}

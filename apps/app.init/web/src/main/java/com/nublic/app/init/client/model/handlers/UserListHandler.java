package com.nublic.app.init.client.model.handlers;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.nublic.app.init.client.model.User;

public interface UserListHandler extends EventHandler {
	public void onUserList(List<User> userList);
}

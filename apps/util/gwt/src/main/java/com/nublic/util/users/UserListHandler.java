package com.nublic.util.users;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;

public interface UserListHandler extends EventHandler {
	public void onUserList(List<User> userList);
}

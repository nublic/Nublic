package com.nublic.util.users;

import com.google.gwt.event.shared.EventHandler;

public interface CheckUserHandler extends EventHandler {
	public void onUserChecked(String userName, boolean available);
}

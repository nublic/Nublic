package com.nublic.app.init.client.model.handlers;

import com.google.gwt.event.shared.EventHandler;

public interface AddUserHandler extends EventHandler {
	public void onUserAdded(String name);
}

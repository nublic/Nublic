package com.nublic.util.widgets;

import com.google.gwt.event.shared.EventHandler;

public interface AddUserHandler extends EventHandler {
	public void onUserAdded(String systemName, String shownName);
}

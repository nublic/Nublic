package com.nublic.util.widgets;

import com.google.gwt.event.shared.EventHandler;

public interface CheckUserHandler extends EventHandler {
	public void onUserChecked(String userName, boolean available);
}

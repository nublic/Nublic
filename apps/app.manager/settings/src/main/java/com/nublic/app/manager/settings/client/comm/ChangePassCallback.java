package com.nublic.app.manager.settings.client.comm;

import com.google.gwt.event.shared.EventHandler;

public interface ChangePassCallback extends EventHandler {
	public void onPasswordChanged(boolean succeed);
}

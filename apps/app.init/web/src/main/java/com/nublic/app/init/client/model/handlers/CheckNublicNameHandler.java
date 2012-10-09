package com.nublic.app.init.client.model.handlers;

import com.google.gwt.event.shared.EventHandler;

public interface CheckNublicNameHandler extends EventHandler {
	public void onNublicNameChecked(String nublicName, boolean available);
}

package com.nublic.app.music.client.datamodel.handlers;

import com.google.gwt.event.shared.EventHandler;

public interface SongsChangeHandler extends EventHandler {
	public void onSongsChange(int from, int to);
}

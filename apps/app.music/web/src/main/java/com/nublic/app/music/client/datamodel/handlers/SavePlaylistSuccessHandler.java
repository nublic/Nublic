package com.nublic.app.music.client.datamodel.handlers;

import com.google.gwt.event.shared.EventHandler;

public interface SavePlaylistSuccessHandler extends EventHandler {
	public void onSaveSuccess(String newPlaylistId);
}

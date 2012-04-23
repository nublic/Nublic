package com.nublic.app.music.client.datamodel.handlers;

import com.google.gwt.event.shared.EventHandler;

public interface MoveSongHandler extends EventHandler {
	public void onSongMoved(String playlistId, int from, int to);
}

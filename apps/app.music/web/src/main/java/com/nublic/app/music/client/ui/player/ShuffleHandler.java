package com.nublic.app.music.client.ui.player;

import com.google.gwt.event.shared.EventHandler;

public interface ShuffleHandler extends EventHandler {
	public void onShuffleToggled(boolean active);
}

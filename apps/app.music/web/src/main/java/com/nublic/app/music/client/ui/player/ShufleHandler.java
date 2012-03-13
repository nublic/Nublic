package com.nublic.app.music.client.ui.player;

import com.google.gwt.event.shared.EventHandler;

public interface ShufleHandler extends EventHandler {
	public void onShufleToggled(boolean active);
}

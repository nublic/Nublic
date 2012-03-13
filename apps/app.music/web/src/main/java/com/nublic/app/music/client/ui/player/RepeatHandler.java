package com.nublic.app.music.client.ui.player;

import com.google.gwt.event.shared.EventHandler;

public interface RepeatHandler extends EventHandler {
	public void onRepeatToggled(boolean active);
}

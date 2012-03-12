package com.nublic.app.music.client.ui.player;

import com.google.gwt.event.shared.EventHandler;

public interface VolumeHandler extends EventHandler {
	public void onVolumeChange(double newVolume);
}

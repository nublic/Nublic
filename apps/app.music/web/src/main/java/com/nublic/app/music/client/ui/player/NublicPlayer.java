package com.nublic.app.music.client.ui.player;

import com.bramosystems.oss.player.core.client.LoadException;
import com.bramosystems.oss.player.core.client.Plugin;
import com.bramosystems.oss.player.core.client.PluginNotFoundException;
import com.bramosystems.oss.player.core.client.PluginVersionException;
import com.bramosystems.oss.player.core.client.skin.CustomAudioPlayer;

public class NublicPlayer extends CustomAudioPlayer {

	public NublicPlayer(Plugin playerPlugin, String mediaURL, String height, String width)
			throws PluginNotFoundException, PluginVersionException, LoadException {
		super(playerPlugin, mediaURL, false, height, width);
		// TODO Auto-generated constructor stub
		
		setPlayerControlWidget(new PlayerLayout());
	}

}

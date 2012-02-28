package com.nublic.app.music.client.ui.player;

import com.bramosystems.oss.player.core.client.LoadException;
import com.bramosystems.oss.player.core.client.PlayerUtil;
import com.bramosystems.oss.player.core.client.Plugin;
import com.bramosystems.oss.player.core.client.PluginNotFoundException;
import com.bramosystems.oss.player.core.client.PluginVersionException;
import com.bramosystems.oss.player.core.client.skin.CustomAudioPlayer;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;


public class NublicPlayer extends CustomAudioPlayer {
	public static Widget create() {
		try {
			return new NublicPlayer(Plugin.FlashPlayer);
		} catch (Exception e) {
			try {
				return new NublicPlayer(Plugin.Native);
			} catch (PluginNotFoundException e2) {
				return PlayerUtil.getMissingPluginNotice(e2.getPlugin());
			} catch (Exception e3) {
				return new Label(e3.getMessage());
			}
		}
	}
	
	public NublicPlayer(Plugin p) throws PluginNotFoundException, PluginVersionException, LoadException {
		super(p, "", false, "65px", "460px");

		setPlayerControlWidget(new PlayerLayout());
//		loadMedia(mediaURL);
	}

}

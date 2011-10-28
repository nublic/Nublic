package com.nublic.app.browser.web.client.UI;

import com.bramosystems.oss.player.core.client.AbstractMediaPlayer;
import com.bramosystems.oss.player.core.client.LoadException;
import com.bramosystems.oss.player.core.client.PlayerUtil;
import com.bramosystems.oss.player.core.client.Plugin;
import com.bramosystems.oss.player.core.client.PluginNotFoundException;
import com.bramosystems.oss.player.core.client.PluginVersionException;
import com.google.gwt.core.client.GWT;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.error.ErrorPopup;
import com.nublic.app.browser.web.client.model.ParamsHashMap;

public class UIUtils {
	static public void showPlayer(ShowsPlayer destination, ParamsHashMap hmap, boolean anyplayer, String type) {
		AbstractMediaPlayer player = null;
		try {
			String path = hmap.get(Constants.PATH_PARAMETER);
			if (path != null) {
				Plugin p = null;
				// if anyplayer is false will try with the flash plugin, which is the "best" one.
				if (anyplayer) {
					p = Plugin.Auto;
				} else {
					p = Plugin.FlashPlayer;
				}
				if (type.equals(Constants.MUSIC_VIEW)) {
					player = PlayerUtil.getPlayer(p,
							GWT.getHostPageBaseURL() + "server/view/" + path + "." + Constants.MUSIC_TYPE,
				    		true, "50px", "300px");
				} else {
					player = PlayerUtil.getPlayer(p,
							GWT.getHostPageBaseURL() + "server/view/" + path + "." + Constants.VIDEO_TYPE,
				    		true);
//				    		, "50px", "300px");
				}

				// TODO: if anyplayer quit lists controls
				destination.showPlayer(player);
			} else {
				ErrorPopup.showError("No path to the resource found");
			}
		} catch(LoadException e) {
		     // catch loading exception and alert user
			ErrorPopup.showError("Error loading file");
		} catch (PluginVersionException e) {
		     // Wrong plugin version
			if (anyplayer) {
				ErrorPopup.showError(PlayerUtil.getMissingPluginNotice(e.getPlugin()));
			} else {
				// If couldn't load with flash plugin we still can try with any other
				UIUtils.showPlayer(destination, hmap, true, type);
			}
		} catch(PluginNotFoundException e) {
			// No plugin found
			if (anyplayer) {
				ErrorPopup.showError(PlayerUtil.getMissingPluginNotice(e.getPlugin()));
			} else {
				UIUtils.showPlayer(destination, hmap, true, type);
			}
		}
	}

}

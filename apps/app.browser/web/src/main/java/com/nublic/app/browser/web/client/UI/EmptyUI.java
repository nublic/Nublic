package com.nublic.app.browser.web.client.UI;

import com.bramosystems.oss.player.core.client.AbstractMediaPlayer;
import com.bramosystems.oss.player.core.client.LoadException;
import com.bramosystems.oss.player.core.client.PlayerUtil;
import com.bramosystems.oss.player.core.client.Plugin;
import com.bramosystems.oss.player.core.client.PluginNotFoundException;
import com.bramosystems.oss.player.core.client.PluginVersionException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.error.ErrorPopup;
import com.nublic.app.browser.web.client.model.ParamsHashMap;

public class EmptyUI extends Composite {

	private static EmptyUIUiBinder uiBinder = GWT.create(EmptyUIUiBinder.class);

	interface EmptyUIUiBinder extends UiBinder<Widget, EmptyUI> {
	}

	@UiField FlexTable rootPanel;
	
	public EmptyUI() {
		initWidget(uiBinder.createAndBindUi(this));
		rootPanel.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);
		rootPanel.getFlexCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);;
	}
	
	public void showPlayer(ParamsHashMap hmap, boolean anyplayer) {
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
				player = PlayerUtil.getPlayer(p,
						GWT.getHostPageBaseURL() + "server/view/" + path + "." + Constants.MUSIC_TYPE,
			    		true, "50px", "300px");

				// TODO: if anyplayer quit lists controls

				rootPanel.setWidget(0, 0, player);
			} else {
				ErrorPopup.showError("No path to the resource found");
			}
		} catch(LoadException e) {
		     // catch loading exception and alert user
			ErrorPopup.showError("File not found");
		} catch (PluginVersionException e) {
		     // catch PluginVersionException, thrown if required plugin version is not found
			if (anyplayer) {
				rootPanel.add(PlayerUtil.getMissingPluginNotice(e.getPlugin()));
			} else {
				// If couldn't load with flash plugin we still can try with any other
				showPlayer(hmap, true);
			}
		} catch(PluginNotFoundException e) {
		     // catch PluginNotFoundException, thrown if no plugin is not found
			if (anyplayer) {
				rootPanel.add(PlayerUtil.getMissingPluginNotice(e.getPlugin()));
			} else {
				showPlayer(hmap, true);
			}
		}
	}

}

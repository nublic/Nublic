package com.scamall.app.widget.mediaplayer;

import com.vaadin.Application;
import com.vaadin.ui.Window;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class MediaplayerTestApplication extends Application {
	@Override
	public void init() {
		Window mainWindow = new Window("flowplayer Example App");

		/* Flowplayer player = new Flowplayer();
		player.setHeight("300px");
		player.setWidth("425px");
		mainWindow.addComponent(player); */

		setMainWindow(mainWindow);
	}

}

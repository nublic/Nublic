package com.scamall.ui.flowplayer;

import com.vaadin.Application;
import com.vaadin.ui.*;

public class FlowplayerWidgetApplication extends Application {
	@Override
	public void init() {
		Window mainWindow = new Window("flowplayer Example App");
		
		Flowplayer player = new Flowplayer();
		player.setHeight("300px");
		player.setWidth("425px");
		mainWindow.addComponent(player);
		
		setMainWindow(mainWindow);
	}

}

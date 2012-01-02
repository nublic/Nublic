package com.nublic.app.manager.settings.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SettingsApp implements EntryPoint {

	SettingsPage theUi;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		initUi();
		Window.enableScrolling(false);
	}
	
	public void initUi() {
		theUi = new SettingsPage();
		RootLayoutPanel rp = RootLayoutPanel.get();
	    rp.add(theUi);
	}	
}

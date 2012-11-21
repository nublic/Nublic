package com.nublic.app.manager.settings.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SettingsApp implements EntryPoint {

	MainUi theUi;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		Document.get().setTitle(Constants.I18N.title());
		initUi();
		Window.enableScrolling(false);
	}
	
	public void initUi() {
		theUi = new MainUi();
		RootLayoutPanel rp = RootLayoutPanel.get();
	    rp.add(theUi);
	}	
}

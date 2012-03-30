package com.nublic.app.photos.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.nublic.app.photos.web.client.view.MainUi;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PhotosApp implements EntryPoint {

	MainUi theUi;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		initUi();
		Window.enableScrolling(false);
	}
	
	public void initUi() {
		theUi = new MainUi();
		RootLayoutPanel rp = RootLayoutPanel.get();
	    rp.add(theUi);
	}
}

package com.nublic.app.photos.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PhotosApp implements EntryPoint {

	// WelcomePage theUi;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		initUi();
		Window.enableScrolling(false);
	}
	
	public void initUi() {
		// theUi = new WelcomePage();
		RootLayoutPanel rp = RootLayoutPanel.get();
	    // rp.add(theUi);
		
		ShowAsCellsWidget w = new ShowAsCellsWidget(-1);
		rp.add(w);
	}	
}

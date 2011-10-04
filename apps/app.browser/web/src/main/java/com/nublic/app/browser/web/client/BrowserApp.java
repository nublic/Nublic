package com.nublic.app.browser.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class BrowserApp implements EntryPoint {
	
	BrowserUi theUi;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		// Create the model
		BrowserModel model = new BrowserModel();
		
		theUi = new BrowserUi(model);
		RootLayoutPanel rp = RootLayoutPanel.get();
	    rp.add(theUi);

	}
	
}

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
		theUi = new BrowserUi();
		RootLayoutPanel rp = RootLayoutPanel.get();
	    rp.add(theUi);
	}
}

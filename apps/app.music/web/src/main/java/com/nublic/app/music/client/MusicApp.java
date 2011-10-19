package com.nublic.app.music.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MusicApp implements EntryPoint {

	MusicUi theUi;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		theUi = new MusicUi();
		RootLayoutPanel rp = RootLayoutPanel.get();
	    rp.add(theUi);
	}
}

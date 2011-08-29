package com.nublic.app.example.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ExampleApp implements EntryPoint {

	ExampleUi theUi;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		theUi = new ExampleUi();
		RootPanel.get("theApp").add(theUi);
	}
}

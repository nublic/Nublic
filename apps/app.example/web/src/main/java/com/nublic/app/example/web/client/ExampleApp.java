package com.nublic.app.example.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ExampleApp implements EntryPoint, ValueChangeHandler<String> {

	ExampleUi theUi;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		theUi = new ExampleUi();
		RootPanel.get("theApp").add(theUi);

		// Initialize History
		String startingToken = History.getToken();
		History.addValueChangeHandler(this);
		History.newItem(startingToken, true);
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		// Method called when History changes,
		// that is, the user clicks Back or Forward
		theUi.showName(event.getValue());
	}
}

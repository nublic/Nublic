package com.nublic.app.manager.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ManagerApp implements EntryPoint, ValueChangeHandler<String> {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
	    String startingToken = History.getToken();
	    History.newItem(startingToken);
	    History.addValueChangeHandler(this);
	    History.fireCurrentHistoryState();
	}
	
	public void initBrowser() {
		// Create the model and the UI
		// model = new BrowserModel();
		// theUi = new BrowserUi(model);

		// RootLayoutPanel rp = RootLayoutPanel.get();
	    // rp.add(theUi);
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		String token = event.getValue();
	}
	
}

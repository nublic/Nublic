package com.nublic.app.manager.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ManagerApp implements EntryPoint, ValueChangeHandler<String> {

	ManagerUi theUi;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		initUi();
		Window.enableScrolling(false);
		// Initialize tokens
		String startingToken = History.getToken();
	    History.newItem(startingToken);
	    History.addValueChangeHandler(this);
	    History.fireCurrentHistoryState();
	}
	
	public void initUi() {
		theUi = new ManagerUi();
		RootLayoutPanel rp = RootLayoutPanel.get();
	    rp.add(theUi);
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		// Handle token
		String token = event.getValue();
		if (token.isEmpty()) {
			theUi.go("welcome");
		} else {
			theUi.go(token);
		}
	}
	
}

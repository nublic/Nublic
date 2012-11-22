package com.nublic.app.manager.settings.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.nublic.app.manager.settings.client.ui.MainUi;
import com.nublic.util.messages.ParamsHashMap;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SettingsApp implements EntryPoint, ValueChangeHandler<String> {

	MainUi theUi;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		Document.get().setTitle(Constants.I18N.title());
		Controller.create();
		Model.create();
		initUi();
		Window.enableScrolling(false);
	
		String startingToken = History.getToken();
	    History.newItem(startingToken);
	    History.addValueChangeHandler(this);
	    History.fireCurrentHistoryState();
	}
	
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		String args = event.getValue();
		ParamsHashMap hmap = new ParamsHashMap(args);
		
		Controller.INSTANCE.changeState(hmap);
	}
	
	public void initUi() {
		theUi = MainUi.create();
		RootLayoutPanel rp = RootLayoutPanel.get();
	    rp.add(theUi);
	}	
}

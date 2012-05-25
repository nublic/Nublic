package com.nublic.app.market.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.nublic.app.market.web.client.controller.Controller;
import com.nublic.app.market.web.client.model.DataModel;
import com.nublic.app.market.web.client.ui.MainUI;
import com.nublic.util.messages.ParamsHashMap;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MarketApp implements EntryPoint, ValueChangeHandler<String> {
	MainUI ui;
	DataModel model;
	
	/**
	 * This is the entry point method.
	 */	
	public void onModuleLoad() {
		// Document.get().setTitle(Constants.I18N.title());
		model = new DataModel();
		ui = new MainUI();
		Controller.create(ui, model);
		
		RootLayoutPanel rp = RootLayoutPanel.get();
	    rp.add(ui);

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
}

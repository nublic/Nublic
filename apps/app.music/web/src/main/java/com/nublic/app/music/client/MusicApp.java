package com.nublic.app.music.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.ui.MainUi;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MusicApp implements EntryPoint, ValueChangeHandler<String> {
	DataModel model;
	MainUi ui;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		model = new DataModel();
		ui = new MainUi(model);
		
		RootLayoutPanel rp = RootLayoutPanel.get();
	    rp.add(ui);

	    String startingToken = History.getToken();
	    History.newItem(startingToken);
	    History.addValueChangeHandler(this);
	    History.fireCurrentHistoryState();
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		
	}

}

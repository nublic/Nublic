package com.nublic.app.init.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.nublic.app.init.client.controller.Controller;
import com.nublic.app.init.client.model.InitModel;
import com.nublic.app.init.client.ui.MainUi;
import com.nublic.util.messages.ParamsHashMap;

public class InitApp implements EntryPoint, ValueChangeHandler<String> {
	InitModel model;
	MainUi ui;

	public void onModuleLoad() {
		model = InitModel.create();
		ui = MainUi.create();
		Controller.create(model, ui);
		
		RootLayoutPanel rp = RootLayoutPanel.get();
	    rp.add(ui);

	    Window.setTitle(Constants.I18N.title());
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

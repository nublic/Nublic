package com.nublic.app.init.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.init.client.model.Step;

public class MainUi extends Composite {
	private static MainUiUiBinder uiBinder = GWT.create(MainUiUiBinder.class);
	interface MainUiUiBinder extends UiBinder<Widget, MainUi> {}

	@UiField StepPanel stepPanel;
	@UiField SimplePanel mainContainer;
	
	public MainUi() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void setStep(Step s) {
		stepPanel.setStep(s);
		mainContainer.setWidget(s.getUiWidget());
	}
}

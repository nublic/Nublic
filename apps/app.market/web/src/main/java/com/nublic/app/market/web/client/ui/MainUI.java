package com.nublic.app.market.web.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class MainUI extends Composite {
	private static MainUIUiBinder uiBinder = GWT.create(MainUIUiBinder.class);
	interface MainUIUiBinder extends UiBinder<Widget, MainUI> { }
	
	@UiField FlowPanel appPanel;

	public MainUI() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}

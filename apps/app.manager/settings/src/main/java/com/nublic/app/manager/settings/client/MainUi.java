package com.nublic.app.manager.settings.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class MainUi extends Composite {
	private static MainUiUiBinder uiBinder = GWT.create(MainUiUiBinder.class);
	interface MainUiUiBinder extends UiBinder<Widget, MainUi> {	}

	public static MainUi INSTANCE = null;
	@UiField NavPanel navPanel;
	
	public static MainUi create() {
		if (INSTANCE == null) {
			INSTANCE = new MainUi();
		}
		return INSTANCE;
	}
	
	public MainUi() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void selectCategory(Category c) {
		navPanel.select(c);
	}

}

package com.nublic.app.photos.web.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.photos.web.client.view.navigation.NavigationPanel;
import com.nublic.util.error.ErrorPopup;

public class MainUi extends Composite {
	private static MainUiUiBinder uiBinder = GWT.create(MainUiUiBinder.class);
	interface MainUiUiBinder extends UiBinder<Widget, MainUi> {}
	
	@UiField SimplePanel mainPanel;
	@UiField NavigationPanel navigationPanel;
	
	boolean currentPlaylistBeingShown = false;

	public MainUi() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void setInnerWidget(Widget w) {
		mainPanel.clear();
		mainPanel.add(w);
	}
	
	public NavigationPanel getNavigationPanel() {
		return navigationPanel;
	}
	
	public void error(String message) {
		ErrorPopup.showError(message);
	}

}

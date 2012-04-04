package com.nublic.app.photos.web.client.view;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.photos.web.client.controller.ThumbnailDragHandler;
import com.nublic.app.photos.web.client.view.navigation.NavigationPanel;
import com.nublic.util.error.ErrorPopup;

public class MainUi extends Composite {
	private static MainUiUiBinder uiBinder = GWT.create(MainUiUiBinder.class);
	interface MainUiUiBinder extends UiBinder<Widget, MainUi> {}
	
	@UiField SimplePanel mainPanel;
	@UiField(provided=true) NavigationPanel navigationPanel;
	
	boolean currentPlaylistBeingShown = false;
	
	static final int DRAG_START_SENSITIVITY = 5;
	PickupDragController dragController;

	public MainUi() {
		navigationPanel = new NavigationPanel(this);
		
		initWidget(uiBinder.createAndBindUi(this));
		
		// Initialize drag and drop
		this.dragController = new PickupDragController(RootPanel.get(), false);
		dragController.setBehaviorDragProxy(true);
		dragController.setBehaviorDragStartSensitivity(DRAG_START_SENSITIVITY);
		dragController.addDragHandler(new ThumbnailDragHandler());
	}
	
	public void setInnerWidget(Widget w) {
		if (mainPanel.getWidget() != null && mainPanel.getWidget() instanceof DisposableWidget) {
			((DisposableWidget)mainPanel.getWidget()).dispose();
		}
		mainPanel.setWidget(w);
	}
	
	public Widget getInnerWidget() {
		return mainPanel.getWidget();
	}
	
	public NavigationPanel getNavigationPanel() {
		return navigationPanel;
	}
	
	public PickupDragController getDragController() {
		return dragController;
	}
	
	public void error(String message) {
		ErrorPopup.showError(message);
	}

}

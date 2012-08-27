package com.nublic.app.photos.web.client.view;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.photos.web.client.dnd.PhotoDragController;
import com.nublic.app.photos.web.client.dnd.ThumbnailDragHandler;
import com.nublic.app.photos.web.client.view.navigation.NavigationPanel;
import com.nublic.util.error.ErrorPopup;

public class MainUi extends Composite {
	private static MainUiUiBinder uiBinder = GWT.create(MainUiUiBinder.class);
	interface MainUiUiBinder extends UiBinder<Widget, MainUi> {}
	
	@UiField SimplePanel mainPanel;
	@UiField(provided=true) NavigationPanel navigationPanel;
	
	boolean currentPlaylistBeingShown = false;

	PhotoDragController dragController;

	public MainUi() {
		navigationPanel = new NavigationPanel(this);
		
		initWidget(uiBinder.createAndBindUi(this));
		
		// Initialize drag and drop
		dragController = new PhotoDragController();
		dragController.addDragHandler(new ThumbnailDragHandler());
	}

	public void setInnerWidget(Widget w) {
		try {
			if (mainPanel.getWidget() != null && mainPanel.getWidget() instanceof DisposableWidget) {
				((DisposableWidget)mainPanel.getWidget()).dispose();
			}
		} catch (Exception e) {
			// Window.alert(e.getMessage());
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

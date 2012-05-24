package com.nublic.app.browser.web.client.UI.dnd;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.UI.BrowserUi;

public class FileDragController extends PickupDragController implements HasProxy {

	FileProxy proxy;
	BrowserUi controller;
	
	public FileDragController(BrowserUi controller) {
		super(RootPanel.get(), false);
		setBehaviorDragProxy(true);
		setBehaviorDragStartSensitivity(Constants.DRAG_START_SENSITIVIY);
		this.controller = controller;
	}
	
	@Override
	protected Widget newDragProxy(DragContext context) {
		proxy = new FileProxy(controller.getSelectedFiles());
		return proxy;
	}

	@Override
	public DragProxy getProxy() {
		return proxy;
	}

}

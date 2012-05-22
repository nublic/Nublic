package com.nublic.app.browser.web.client.UI.dnd;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.UI.FileWidget;
import com.nublic.app.browser.web.client.UI.actions.PasteAction;

public class FolderDropController extends AbstractDropController {
	BrowserUi stateProvider;
	FileWidget dropTarget;
	DragProxy proxy;

	public FolderDropController(FileWidget dropTarget, BrowserUi stateProvider) {
		super(dropTarget);
		this.dropTarget = dropTarget;
		this.stateProvider = stateProvider;
	}

	@Override
	public void onDrop(DragContext context) {
//		super.onDrop(context);
		PasteAction.doPasteAction("copy", stateProvider.getSelectedFiles(), dropTarget.getPath(), stateProvider.getModel(), stateProvider);
	}

	@Override
	public void onEnter(DragContext context) {
		proxy = ((FileDragController)context.dragController).getProxy();
		proxy.setState(ProxyState.COPY);
		dropTarget.addStyleName(Constants.CSS_SELECTED);
	}

	@Override
	public void onLeave(DragContext context) {
		proxy.setState(ProxyState.NONE);
		dropTarget.removeStyleName(Constants.CSS_SELECTED);
	}

}

package com.nublic.app.browser.web.client.UI;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.UI.actions.PasteAction;

public class FolderDropController extends AbstractDropController {
	BrowserUi stateProvider;
	FileWidget dropTarget;
	
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
		dropTarget.addStyleName(Constants.CSS_SELECTED);
	}

	@Override
	public void onLeave(DragContext context) {
		dropTarget.removeStyleName(Constants.CSS_SELECTED);
	}
	
	

}

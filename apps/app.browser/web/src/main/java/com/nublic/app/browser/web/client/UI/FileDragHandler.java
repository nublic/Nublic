package com.nublic.app.browser.web.client.UI;

import com.allen_sauer.gwt.dnd.client.DragHandlerAdapter;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;

public class FileDragHandler extends DragHandlerAdapter {
	BrowserUi stateProvider;
	
	public FileDragHandler(BrowserUi stateProvider) {
		this.stateProvider = stateProvider;
	}
	
//	@Override
//	public void onDragEnd(DragEndEvent event) {
//	}

	@Override
	public void onDragStart(DragStartEvent event) {
		FileWidget w = ((FileWidget)event.getSource());
		w.setChecked(true);
		stateProvider.getSelectedFiles().add(w);
		stateProvider.notifyContextHandlers();
	}
}

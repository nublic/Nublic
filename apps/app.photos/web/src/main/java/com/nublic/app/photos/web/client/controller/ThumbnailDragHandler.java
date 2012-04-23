package com.nublic.app.photos.web.client.controller;

import com.allen_sauer.gwt.dnd.client.DragHandlerAdapter;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.nublic.app.photos.web.client.view.album.ThumbnailWidget;

public class ThumbnailDragHandler extends DragHandlerAdapter {

	@Override
	public void onDragStart(DragStartEvent event) {
		ThumbnailWidget w = ((ThumbnailWidget)event.getSource());
		w.setChecked(true);
	}
}

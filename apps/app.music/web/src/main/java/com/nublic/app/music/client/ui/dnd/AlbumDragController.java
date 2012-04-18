package com.nublic.app.music.client.ui.dnd;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.ui.dnd.proxy.AlbumDragProxy;
import com.nublic.app.music.client.ui.dnd.proxy.DragProxy;
import com.nublic.app.music.client.ui.dnd.proxy.HasProxy;

public class AlbumDragController extends PickupDragController implements HasProxy {

	String draggingAlbumId;
	AlbumDragProxy proxy = null;

	public AlbumDragController() {
		super(RootPanel.get(), false);
	    setBehaviorDragProxy(true);
	    setBehaviorMultipleSelection(false);
	    setBehaviorDragStartSensitivity(5);
	}

	@Override
	protected Widget newDragProxy(DragContext context) {
		draggingAlbumId = ((HasAlbumId) context.draggable).getAlbumId();

		proxy = new AlbumDragProxy(draggingAlbumId);
		return proxy;
	}
	
	public String getDraggingAlbumId() {
		return draggingAlbumId;
	}


	@Override
	public DragProxy getProxy() {
		return proxy;
	}
}

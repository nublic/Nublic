package com.nublic.app.music.client.ui.dnd;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.ui.dnd.proxy.DragProxy;
import com.nublic.app.music.client.ui.dnd.proxy.HasProxy;

public class AlbumDragController extends PickupDragController implements HasProxy {

//	int draggingRow = -1;
//	SongInfo draggingSong = null;
	DragProxy proxy = null;
	
	public AlbumDragController() {
		super(RootPanel.get(), false);
	    setBehaviorDragProxy(true);
	    setBehaviorMultipleSelection(false);
	    setBehaviorDragStartSensitivity(5);
	}
	
	@Override
	protected Widget newDragProxy(DragContext context) {
//		draggingRow = ((DraggableSong) context.draggable).getRow();
//		draggingSong = ((DraggableSong) context.draggable).getSong();

//		proxy = new DragProxy(draggingSong);
//		return proxy;
		return null;
	}

//	public int getDraggingRow() {
//		return draggingRow;
//	}
//	
//	public SongInfo getDraggingSong() {
//		return draggingSong;
//	}

	@Override
	public DragProxy getProxy() {
		return proxy;
	}
}

package com.nublic.app.music.client.ui.dnd;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.ui.dnd.proxy.ArtistDragProxy;
import com.nublic.app.music.client.ui.dnd.proxy.DragProxy;
import com.nublic.app.music.client.ui.dnd.proxy.HasProxy;

public class ArtistDragController extends PickupDragController implements HasProxy {
	String draggingArtistId;
	String draggingCollectionId;
	int numberOfSongs;
	ArtistDragProxy proxy = null;

	public ArtistDragController() {
		super(RootPanel.get(), false);
	    setBehaviorDragProxy(true);
	    setBehaviorMultipleSelection(false);
	    setBehaviorDragStartSensitivity(5);
	}

	@Override
	protected Widget newDragProxy(DragContext context) {
		draggingArtistId = ((HasArtistInfo) context.draggable).getArtistId();
		draggingCollectionId = ((HasArtistInfo) context.draggable).getCollectionId();
		numberOfSongs = ((HasArtistInfo) context.draggable).getNumberOfSongs();

		proxy = new ArtistDragProxy(draggingArtistId, numberOfSongs);
		return proxy;
	}
	
	public String getDraggingArtistId() {
		return draggingArtistId;
	}
	
	public String getDraggingCollectionId() {
		return draggingCollectionId;
	}

	@Override
	public DragProxy getProxy() {
		return proxy;
	}
}

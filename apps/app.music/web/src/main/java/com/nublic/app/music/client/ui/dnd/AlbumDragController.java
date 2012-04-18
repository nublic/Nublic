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
	String draggingArtistId;
	String draggingCollectionId;
	int numberOfSongs;
	AlbumDragProxy proxy = null;

	public AlbumDragController() {
		super(RootPanel.get(), false);
	    setBehaviorDragProxy(true);
	    setBehaviorMultipleSelection(false);
	    setBehaviorDragStartSensitivity(5);
	}

	@Override
	protected Widget newDragProxy(DragContext context) {
		draggingAlbumId = ((HasAlbumInfo) context.draggable).getAlbumId();
		draggingArtistId = ((HasAlbumInfo) context.draggable).getArtistId();
		draggingCollectionId = ((HasAlbumInfo) context.draggable).getCollectionId();
		numberOfSongs = ((HasAlbumInfo) context.draggable).getNumberOfSongs();

		proxy = new AlbumDragProxy(draggingAlbumId, draggingArtistId, numberOfSongs);
		return proxy;
	}
	
	public String getDraggingAlbumId() {
		return draggingAlbumId;
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

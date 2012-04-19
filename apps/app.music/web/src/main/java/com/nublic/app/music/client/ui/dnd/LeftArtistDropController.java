package com.nublic.app.music.client.ui.dnd;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.nublic.app.music.client.controller.Controller;
import com.nublic.app.music.client.ui.NavigationPanel;

public class LeftArtistDropController extends LeftDropController {

	public LeftArtistDropController(NavigationPanel dropTarget) {
		super(dropTarget);
	}

	@Override
	public void dropInCollection(DragContext context, String collectionId) {
		Controller.INSTANCE.addToCollection(overTag.getId(), getArtist(context), null, getCollection(context));
	}

	@Override
	public void dropInPlaylist(DragContext context, String playlistId) {
		Controller.INSTANCE.addAtEndOfPlaylist(overTag.getId(), getArtist(context), null, getCollection(context));	
	}


	private String getArtist(DragContext context) {
		ArtistDragController sDragController = (ArtistDragController) context.dragController;
		return sDragController.getDraggingArtistId();
	}

	private String getCollection(DragContext context) {
		ArtistDragController sDragController = (ArtistDragController) context.dragController;
		return sDragController.getDraggingCollectionId();
	}

}
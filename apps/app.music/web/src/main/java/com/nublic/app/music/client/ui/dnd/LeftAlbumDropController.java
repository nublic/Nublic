package com.nublic.app.music.client.ui.dnd;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.nublic.app.music.client.datamodel.Controller;
import com.nublic.app.music.client.ui.NavigationPanel;

public class LeftAlbumDropController extends LeftDropController {

	public LeftAlbumDropController(NavigationPanel dropTarget) {
		super(dropTarget);
	}

	@Override
	public void dropInCollection(DragContext context, String collectionId) {
		Controller.INSTANCE.addToCollection(overTag.getId(), getArtist(context), getAlbum(context), getCollection(context));
	}

	@Override
	public void dropInPlaylist(DragContext context, String playlistId) {
		Controller.INSTANCE.addAtEndOfPlaylist(overTag.getId(), getArtist(context), getAlbum(context), getCollection(context));	
	}

	private String getAlbum(DragContext context) {
		AlbumDragController sDragController = (AlbumDragController) context.dragController;
		return sDragController.getDraggingAlbumId();
	}

	private String getArtist(DragContext context) {
		AlbumDragController sDragController = (AlbumDragController) context.dragController;
		return sDragController.getDraggingArtistId();
	}

	private String getCollection(DragContext context) {
		AlbumDragController sDragController = (AlbumDragController) context.dragController;
		return sDragController.getDraggingCollectionId();
	}

}
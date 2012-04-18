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
		Controller.INSTANCE.addToCollection(overTag.getId(), getDraggingArtist(context), getDraggingAlbum(context), getDraggingCollection(context));
	}

	@Override
	public void dropInPlaylist(DragContext context, String playlistId) {
		Controller.INSTANCE.addAtEndOfPlaylist(overTag.getId(), getDraggingArtist(context), getDraggingAlbum(context), getDraggingCollection(context));	
	}
	
	private String getDraggingAlbum(DragContext context) {
		AlbumDragController sDragController = (AlbumDragController) context.dragController;
		return sDragController.getDraggingAlbumId();
	}
	
	private String getDraggingArtist(DragContext context) {
		AlbumDragController sDragController = (AlbumDragController) context.dragController;
		return sDragController.getDraggingArtistId();
	}
	
	private String getDraggingCollection(DragContext context) {
		AlbumDragController sDragController = (AlbumDragController) context.dragController;
		return sDragController.getDraggingCollectionId();
	}

}
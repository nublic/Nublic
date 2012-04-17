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
		Controller.INSTANCE.addToCollection(overTag.getId(), null, getDraggingAlbum(context), null);
	}

	@Override
	public void dropInPlaylist(DragContext context, String playlistId) {
		Controller.INSTANCE.addAtEndOfPlaylist(overTag.getId(), null, getDraggingAlbum(context), null);	
	}
	
	private String getDraggingAlbum(DragContext context) {
//		SongDragController sDragController = (SongDragController) context.dragController;
//		return sDragController.getDraggingSong();
		return null;
	}

}
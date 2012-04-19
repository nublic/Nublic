package com.nublic.app.music.client.ui.dnd;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.nublic.app.music.client.controller.Controller;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.app.music.client.ui.NavigationPanel;

public class LeftSongDropController extends LeftDropController {

	public LeftSongDropController(NavigationPanel dropTarget) {
		super(dropTarget);
	}

	@Override
	public void dropInCollection(DragContext context, String collectionId) {
		Controller.INSTANCE.addToCollection(overTag.getId(), getDraggingSong(context));
	}

	@Override
	public void dropInPlaylist(DragContext context, String playlistId) {
		Controller.INSTANCE.addAtEndOfPlaylist(overTag.getId(), getDraggingSong(context));	
	}
	
	private SongInfo getDraggingSong(DragContext context) {
		SongDragController sDragController = (SongDragController) context.dragController;
		return sDragController.getDraggingSong();
	}

}

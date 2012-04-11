package com.nublic.app.music.client.ui.dnd;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.nublic.app.music.client.datamodel.Controller;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.app.music.client.ui.NavigationPanel;
import com.nublic.app.music.client.ui.TagWidget;

public class LeftDropController extends AbstractDropController {

	NavigationPanel dropTarget;
	TagWidget originalySelected;
	TagWidget overTag;
	
	public LeftDropController(NavigationPanel dropTarget) {
		super(dropTarget);
		
		this.dropTarget = dropTarget;
	}

	@Override
	public void onDrop(DragContext context) {
		SongDragController sDragController = (SongDragController) context.dragController;
		SongInfo draggingSong = sDragController.getDraggingSong();
		
		if (overTag != null && overTag.getKind() != null) {
			// If it's defined and it's not "all music" tag
			switch (overTag.getKind()) {
			case COLLECTION:
				Controller.INSTANCE.addToCollection(overTag.getId(), draggingSong);
				break;
			case PLAYLIST:
				Controller.INSTANCE.addAtEndOfPlaylist(overTag.getId(), draggingSong);
				break;
			}
		}

		super.onDrop(context);
	}

	@Override
	public void onEnter(DragContext context) {
		originalySelected = dropTarget.getSelectedTag();
		setNewOverTag(dropTarget.getIntersectionTag(context.mouseX, context.mouseY));
		super.onEnter(context);
	}
	
	@Override
	public void onMove(DragContext context) {
		setNewOverTag(dropTarget.getIntersectionTag(context.mouseX, context.mouseY));
		super.onMove(context);
	}

	private void setNewOverTag(TagWidget intersectionTag) {
		overTag = intersectionTag;
		dropTarget.select(intersectionTag);
	}

	@Override
	public void onLeave(DragContext context) {
		overTag = null;
		dropTarget.select(originalySelected);
		super.onLeave(context);
	}

}

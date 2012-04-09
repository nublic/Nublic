package com.nublic.app.music.client.ui.dnd;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.nublic.app.music.client.ui.NavigationPanel;
import com.nublic.app.music.client.ui.TagWidget;

public class LeftDropController extends AbstractDropController {

	NavigationPanel dropTarget;
	TagWidget originalySelected;
	
	public LeftDropController(NavigationPanel dropTarget) {
		super(dropTarget);
		
		this.dropTarget = dropTarget;
	}

//	private static final String CSS_DEMO_TABLE_POSITIONER = "demo-table-positioner";
//
//	private FlexTable flexTable;
//
//	private Widget positioner = null;
//
//	public FlexTableRowDropController(FlexTable flexTable) {
//		super(flexTable);
//		this.flexTable = flexTable;
//	}
//
//	@Override
//	public void onDrop(DragContext context) {
//		SongDragController sDragController = (SongDragController) context.dragController;
//		Controller.INSTANCE.moveSongInPlaylist(playlistId, sDragController.getDraggingRow(), targetRow);
//		super.onDrop(context);
//	}
//
	@Override
	public void onEnter(DragContext context) {
		originalySelected = dropTarget.getSelectedTag();
		setNewOverItem(dropTarget.getIntersectionTag(context.mouseX, context.mouseY));
	}
	
	@Override
	public void onMove(DragContext context) {
		setNewOverItem(dropTarget.getIntersectionTag(context.mouseX, context.mouseY));
	}


	private void setNewOverItem(TagWidget intersectionTag) {
		dropTarget.select(intersectionTag);
	}

//
//	@Override
//	public void onLeave(DragContext context) {
//		targetRow = -1;
//		super.onLeave(context);
//	}
	
	@Override
	public void onLeave(DragContext context) {
		dropTarget.select(originalySelected);
	}



}

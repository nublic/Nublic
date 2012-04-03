package com.nublic.app.music.client.ui.dnd;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractPositioningDropController;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.datamodel.Controller;

public class SongDropController extends AbstractPositioningDropController {

	int targetRow = -1;
	String playlistId;
	
	public SongDropController(Panel dropTarget, String playlistId) {
		super(dropTarget);
		
		this.playlistId = playlistId;
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
	@Override
	public void onDrop(DragContext context) {
		SongDragController sDragController = (SongDragController) context.dragController;
		Controller.INSTANCE.moveSongInPlaylist(playlistId, sDragController.getDraggingRow(), targetRow);
		super.onDrop(context);
	}
//
//	@Override
//	public void onEnter(DragContext context) {
//		super.onEnter(context);
//		positioner = newPositioner(context);
//	}
//
	@Override
	public void onLeave(DragContext context) {
//		positioner.removeFromParent();
//		positioner = null;
		targetRow = -1;
		super.onLeave(context);
	}
//
	@Override
	public void onMove(DragContext context) {
		super.onMove(context);
		targetRow = findRowOnGrid(((Grid)context.draggable.getParent()), context.mouseX, context.mouseY);
		

//		if (flexTable.getRowCount() > 0) {
//			Widget w = flexTable.getWidget(targetRow == -1 ? 0 : targetRow, 0);
//			Location widgetLocation = new WidgetLocation(w,
//					context.boundaryPanel);
//			Location tableLocation = new WidgetLocation(flexTable,
//					context.boundaryPanel);
//			context.boundaryPanel.add(
//					positioner,
//					tableLocation.getLeft(),
//					widgetLocation.getTop()
//							+ (targetRow == -1 ? 0 : w.getOffsetHeight()));
//		}
	}
//
//	Widget newPositioner(DragContext context) {
//		Widget p = new SimplePanel();
//		p.addStyleName(CSS_DEMO_TABLE_POSITIONER);
//		p.setPixelSize(flexTable.getOffsetWidth(), 1);
//		return p;
//	}

	private int findRowOnGrid(Grid grid, int mouseX, int mouseY) {
		boolean found = false; 
		int i = 0;
		
		while (i < grid.getRowCount() && !found) {
			Widget w = grid.getWidget(i, 0);
			if (mouseY > w.getAbsoluteTop() + (w.getOffsetHeight() / 2)) {
				i++;
			} else {
				found = true;
			}
		}
		return i;
	}

}

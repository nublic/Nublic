package com.nublic.app.music.client.ui.dnd;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractPositioningDropController;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.datamodel.Controller;

public class ListDropController extends AbstractPositioningDropController {

	int lastTarget = -1;
	int targetRow = -1;
	String playlistId;
	
	public ListDropController(Panel dropTarget, String playlistId) {
		super(dropTarget);
		
		this.playlistId = playlistId;
	}

	@Override
	public void onDrop(DragContext context) {
		removeLocalizer((Grid)context.draggable.getParent());
		SongDragController sDragController = (SongDragController) context.dragController;
		Controller.INSTANCE.moveSongInPlaylist(playlistId, sDragController.getDraggingRow(), targetRow);
		super.onDrop(context);
	}

	@Override
	public void onLeave(DragContext context) {
		removeLocalizer((Grid)context.draggable.getParent());
		targetRow = -1;
		super.onLeave(context);
	}
//
	@Override
	public void onMove(DragContext context) {
		super.onMove(context);
		Grid grid = (Grid)context.draggable.getParent();
		targetRow = findRowOnGrid(grid, context.mouseX, context.mouseY);
		setLocalizer(grid);
	}

	private void setLocalizer(Grid grid) {
		if (lastTarget != targetRow) {
			removeLocalizer(grid);
	
			// Set the new one
			if (targetRow != -1 && targetRow != grid.getRowCount()) {
				grid.getRowFormatter().addStyleName(targetRow, "insertup");
			} else if (targetRow == grid.getRowCount()) {
				grid.getRowFormatter().addStyleName(targetRow -1, "insertdown");
			}
			lastTarget = targetRow;
		}
	}
		
	
	private void removeLocalizer(Grid grid) {
		// Remove previous localizer
		if (lastTarget != -1 && lastTarget != grid.getRowCount()) {
			grid.getRowFormatter().removeStyleName(lastTarget, "insertup");
		} else if (lastTarget == grid.getRowCount()) {
			grid.getRowFormatter().removeStyleName(lastTarget -1, "insertdown");
		}
		lastTarget = -1;
	}

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

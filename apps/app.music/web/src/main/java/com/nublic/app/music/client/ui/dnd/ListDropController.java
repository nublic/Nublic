package com.nublic.app.music.client.ui.dnd;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractPositioningDropController;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.datamodel.Controller;
import com.nublic.app.music.client.ui.dnd.proxy.DragProxy;
import com.nublic.app.music.client.ui.dnd.proxy.ProxyState;

public class ListDropController extends AbstractPositioningDropController {

	int lastTarget = -1;
	int targetRow = -1;
	int draggingRow = -1;
	String playlistId;
	DragProxy proxy = null;
	
	public ListDropController(Panel dropTarget, String playlistId) {
		super(dropTarget);
		
		this.playlistId = playlistId;
	}
	
	@Override
	public void onEnter(DragContext context) {
		// To give feedback on proxy
		proxy = ((SongDragController) context.dragController).getProxy();
		draggingRow = ((SongDragController) context.dragController).getDraggingRow();
		super.onEnter(context);
	}

	@Override
	public void onDrop(DragContext context) {
		removeLocalizer((Grid)context.draggable.getParent());
		SongDragController sDragController = (SongDragController) context.dragController;
		Controller.INSTANCE.moveSongInPlaylist(playlistId, sDragController.getDraggingRow(), targetRow);
		super.onDrop(context);
	}

	@Override
	public void onMove(DragContext context) {
		Grid grid = (Grid)context.draggable.getParent();
		targetRow = findRowOnGrid(grid, context.mouseX, context.mouseY);
		setLocalizer(grid);
		super.onMove(context);
	}
	
	@Override
	public void onLeave(DragContext context) {
		removeLocalizer((Grid)context.draggable.getParent());
		proxy.setState(ProxyState.NONE);
		targetRow = -1;
		super.onLeave(context);
	}

	private void setLocalizer(Grid grid) {
		if (lastTarget != targetRow) {
			removeLocalizer(grid);
	
			// Set the new one
			if (targetRow != -1 && targetRow != grid.getRowCount()) {
				grid.getRowFormatter().addStyleName(targetRow, "insertup"); // For feedback between the rows.. to know where we are dropping
				// Feedback on proxy
				if (targetRow > draggingRow) {
					proxy.setState(ProxyState.DOWN);
				} else {
					proxy.setState(ProxyState.UP);
				}
			} else if (targetRow == grid.getRowCount()) {
				// Under the last row
				grid.getRowFormatter().addStyleName(targetRow -1, "insertdown");
				proxy.setState(ProxyState.DOWN);
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

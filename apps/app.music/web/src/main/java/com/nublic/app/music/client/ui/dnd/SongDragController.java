package com.nublic.app.music.client.ui.dnd;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class SongDragController extends PickupDragController {

	int draggingRow = -1;
	
	public SongDragController() {
		super(RootPanel.get(), false);
	    setBehaviorDragProxy(true);
	    setBehaviorMultipleSelection(false);
	    setBehaviorDragStartSensitivity(5);
	}
	
	@Override
	protected Widget newDragProxy(DragContext context) {
		HorizontalPanel proxy = new HorizontalPanel();
//		proxy.addStyleName(CSS_DEMO_FLEX_TABLE_ROW_EXAMPLE_TABLE_PROXY);
		draggingRow = ((Draggable) context.draggable).getRow();
		copyRow(proxy, (Grid)context.draggable.getParent(), draggingRow);
		
		return proxy;
	}
	
	private void copyRow(HorizontalPanel target, Grid grid, int row) {
		for (int i = 2; i < grid.getColumnCount(); i++) {
			Widget w = grid.getWidget(row, i);
			HTML h = new HTML(w.getElement().getInnerHTML());
			if (w.getStyleName() != null && !w.getStyleName().equals("")) {
				h.addStyleName(w.getStyleName());
			}
			target.add(h);
		}
	}

	public int getDraggingRow() {
		return draggingRow;
	}

}

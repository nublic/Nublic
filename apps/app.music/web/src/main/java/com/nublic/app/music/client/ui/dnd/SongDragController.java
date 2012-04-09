package com.nublic.app.music.client.ui.dnd;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.datamodel.SongInfo;

public class SongDragController extends PickupDragController {

	int draggingRow = -1;
	SongInfo draggingSong = null;
	
	public SongDragController() {
		super(RootPanel.get(), false);
	    setBehaviorDragProxy(true);
	    setBehaviorMultipleSelection(false);
	    setBehaviorDragStartSensitivity(5);
	}
	
	@Override
	protected Widget newDragProxy(DragContext context) {
//		proxy.addStyleName(CSS_DEMO_FLEX_TABLE_ROW_EXAMPLE_TABLE_PROXY);
		draggingRow = ((Draggable) context.draggable).getRow();
		draggingSong = ((Draggable) context.draggable).getSong();
		
//		HorizontalPanel proxy = new HorizontalPanel();
//		proxy.add(new Label(draggingSong.getTitle()));
				
//		return proxy;
		return new Label(draggingSong.getTitle());
	}

	public int getDraggingRow() {
		return draggingRow;
	}
	
	public SongInfo getDraggingSong() {
		return draggingSong;
	}

	@SuppressWarnings("unused")
	@Deprecated
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

	
}

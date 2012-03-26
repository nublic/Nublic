package com.nublic.app.music.client.ui.dnd;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.RootPanel;

public class SongDragController extends PickupDragController {

	public SongDragController() {
		super(RootPanel.get(), false);
	    setBehaviorDragProxy(true);
	    setBehaviorMultipleSelection(false);
	}
	
//	@Override
//	protected Widget newDragProxy(DragContext context) {
//		FlexTable proxy;
//		proxy = new FlexTable();
//		proxy.addStyleName(CSS_DEMO_FLEX_TABLE_ROW_EXAMPLE_TABLE_PROXY);
//		draggableTable = (FlexTable) context.draggable.getParent();
//		dragRow = getWidgetRow(context.draggable, draggableTable);
//		FlexTableUtil.copyRow(draggableTable, proxy, dragRow, 0);
//		return proxy;
//	}

}

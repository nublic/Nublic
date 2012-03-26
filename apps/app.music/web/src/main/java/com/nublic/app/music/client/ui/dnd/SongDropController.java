package com.nublic.app.music.client.ui.dnd;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractPositioningDropController;
import com.google.gwt.user.client.ui.Panel;

public class SongDropController extends AbstractPositioningDropController {

	public SongDropController(Panel dropTarget) {
		super(dropTarget);
	}

//	private static final String CSS_DEMO_TABLE_POSITIONER = "demo-table-positioner";
//
//	private FlexTable flexTable;
//
//	private InsertPanel flexTableRowsAsIndexPanel = new InsertPanel() {
//
//		@Override
//		public void add(Widget w) {
//			throw new UnsupportedOperationException();
//		}
//
//		@Override
//		public Widget getWidget(int index) {
//			return flexTable.getWidget(index, 0);
//		}
//
//		@Override
//		public int getWidgetCount() {
//			return flexTable.getRowCount();
//		}
//
//		@Override
//		public int getWidgetIndex(Widget child) {
//			throw new UnsupportedOperationException();
//		}
//
//		@Override
//		public void insert(Widget w, int beforeIndex) {
//			throw new UnsupportedOperationException();
//		}
//
//		@Override
//		public boolean remove(int index) {
//			throw new UnsupportedOperationException();
//		}
//	};
//
//	private Widget positioner = null;
//
//	private int targetRow;
//
//	public FlexTableRowDropController(FlexTable flexTable) {
//		super(flexTable);
//		this.flexTable = flexTable;
//	}
//
	@Override
	public void onDrop(DragContext context) {
		SongDragController trDragController = (SongDragController) context.dragController;
		// TODO: move row in table
//		FlexTableUtil.moveRow(trDragController.getDraggableTable(), flexTable,
//				trDragController.getDragRow(), targetRow + 1);
		super.onDrop(context);
	}
//
//	@Override
//	public void onEnter(DragContext context) {
//		super.onEnter(context);
//		positioner = newPositioner(context);
//	}
//
//	@Override
//	public void onLeave(DragContext context) {
//		positioner.removeFromParent();
//		positioner = null;
//		super.onLeave(context);
//	}
//
//	@Override
//	public void onMove(DragContext context) {
//		super.onMove(context);
//		targetRow = DOMUtil.findIntersect(flexTableRowsAsIndexPanel,
//				new CoordinateLocation(context.mouseX, context.mouseY),
//				LocationWidgetComparator.BOTTOM_HALF_COMPARATOR) - 1;
//
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
//	}
//
//	Widget newPositioner(DragContext context) {
//		Widget p = new SimplePanel();
//		p.addStyleName(CSS_DEMO_TABLE_POSITIONER);
//		p.setPixelSize(flexTable.getOffsetWidth(), 1);
//		return p;
//	}

}

package com.nublic.app.browser.web.client.UI;

import java.util.ArrayList;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.allen_sauer.gwt.dnd.client.util.CoordinateArea;
import com.allen_sauer.gwt.dnd.client.util.CoordinateLocation;
import com.allen_sauer.gwt.dnd.client.util.Location;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class TreeDropController extends AbstractDropController {
	Tree dropTarget;
	TreeAdapter adapter;
	TreeItem originalySelected;
	
	public TreeDropController(Tree dropTarget, TreeAdapter adapter) {
		super(dropTarget);
		this.dropTarget = dropTarget;
		this.adapter = adapter;
	}

	private TreeItem getMouseOverItem(DragContext context) {
		// We find the intersection between the mouse coordinate and the TreeItem in our tree
		int x = context.mouseX;
		int y = context.mouseY;
		// this list has to be created on this order and iterated inversely
		// (parents elements occupy all the space of their children)
		ArrayList<TreeItem> itemList = adapter.getVisibleList();
		for (int i = itemList.size() -1 ; i >= 0 ; i--) {
			Element e = itemList.get(i).getElement();
			Location l = new CoordinateLocation(x, y);
			CoordinateArea a = new CoordinateArea(e.getAbsoluteLeft(), e.getAbsoluteTop(), e.getAbsoluteRight(), e.getAbsoluteBottom());
			if (a.intersects(l)) {
				return itemList.get(i);
			}
		}
		return null;
	}

	@Override
	public void onDrop(DragContext context) {
	}

	@Override
	public void onEnter(DragContext context) {
//		TreeItem mouseOver = adapter.getMouseOver();
//		if (mouseOver != null) {
//			mouseOver.addStyleName(DragClientBundle.INSTANCE.css()
//					.dropTargetEngage());
//		}
		originalySelected = dropTarget.getSelectedItem();
		TreeItem newSelected = getMouseOverItem(context);
		if (newSelected != null) {
			dropTarget.setSelectedItem(newSelected);
		}
		
	}

	@Override
	public void onLeave(DragContext context) {
//		dropTarget.removeStyleName(DragClientBundle.INSTANCE.css()
//				.dropTargetEngage());
//		TreeItem mouseOver = adapter.getMouseOver();
//		if (mouseOver != null) {
//			mouseOver.removeStyleName(DragClientBundle.INSTANCE.css()
//					.dropTargetEngage());
//		}
		dropTarget.setSelectedItem(originalySelected);
	}

	@Override
	public void onMove(DragContext context) {
		TreeItem newSelected = getMouseOverItem(context);
		if (newSelected != null) {
			dropTarget.setSelectedItem(newSelected);
		}
	}

	@Override
	public void onPreviewDrop(DragContext context) throws VetoDragException {
	}
}

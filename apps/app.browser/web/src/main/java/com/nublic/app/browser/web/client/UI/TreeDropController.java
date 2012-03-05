package com.nublic.app.browser.web.client.UI;

import java.util.ArrayList;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.allen_sauer.gwt.dnd.client.util.CoordinateArea;
import com.allen_sauer.gwt.dnd.client.util.CoordinateLocation;
import com.allen_sauer.gwt.dnd.client.util.Location;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.UI.actions.PasteAction;
import com.nublic.app.browser.web.client.model.FolderNode;

public class TreeDropController extends AbstractDropController {
	Tree dropTarget;
	TreeAdapter adapter;
	TreeItem originalySelected;
	Timer timer = null;
	BrowserUi stateProvider;
	
	public TreeDropController(Tree dropTarget, TreeAdapter adapter, BrowserUi stateProvider) {
		super(dropTarget);
		this.dropTarget = dropTarget;
		this.adapter = adapter;
		this.stateProvider = stateProvider;
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
	
	private void setNewOverItem(final TreeItem mouseOverItem) {
		// To avoid errors when we don't achieve to find the item the mouse is over
		// and, to avoid setting selected something which is already selected
		if (mouseOverItem != null && !mouseOverItem.equals(dropTarget.getSelectedItem())) {
			// Set it selected without firing events
			dropTarget.setSelectedItem(mouseOverItem, false);
			// Cancel previous timers
			if (timer != null) {
				timer.cancel();
			}
			// If has children, start a timer to open the node
			if (mouseOverItem.getChildCount() > 0) {
				timer = new Timer() {
					@Override
					public void run() {
						mouseOverItem.setState(true, true); // open, firing events
					}
				};
				timer.schedule(Constants.TIME_TO_OPEN);
			}
		}
	}

	@Override
	public void onDrop(DragContext context) {
		FolderNode folder = (FolderNode) dropTarget.getSelectedItem().getUserObject();
		if (folder.isWritable()) {
			PasteAction.doPasteAction("copy", stateProvider.getSelectedFiles(), folder.getRealPath(), stateProvider.getModel(), stateProvider);
		}
	}

	@Override
	public void onEnter(DragContext context) {
		originalySelected = dropTarget.getSelectedItem();
		setNewOverItem(getMouseOverItem(context));
	}

	@Override
	public void onLeave(DragContext context) {
		dropTarget.setSelectedItem(originalySelected, false);
		if (timer != null) {
			timer.cancel();
		}
	}

	@Override
	public void onMove(DragContext context) {
		setNewOverItem(getMouseOverItem(context));
	}

	@Override
	public void onPreviewDrop(DragContext context) throws VetoDragException {
	}
}

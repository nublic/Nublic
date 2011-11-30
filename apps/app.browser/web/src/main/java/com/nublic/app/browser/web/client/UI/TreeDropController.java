package com.nublic.app.browser.web.client.UI;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.allen_sauer.gwt.dnd.client.util.DragClientBundle;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class TreeDropController extends AbstractDropController {
	Tree dropTarget;
	TreeAdapter adapter;
	
	public TreeDropController(Tree dropTarget, TreeAdapter adapter) {
		super(dropTarget);
		this.dropTarget = dropTarget;
		this.adapter = adapter;
	}

//	@Override
//	public Widget getDropTarget() {
//		return dropTarget;
//	}

	@Override
	public void onDrop(DragContext context) {
	}

	@Override
	public void onEnter(DragContext context) {
		TreeItem mouseOver = adapter.getMouseOver();
		if (mouseOver != null) {
			mouseOver.getWidget().addStyleName(DragClientBundle.INSTANCE.css()
					.dropTargetEngage());
		}
	}

	@Override
	public void onLeave(DragContext context) {
//		dropTarget.removeStyleName(DragClientBundle.INSTANCE.css()
////				.dropTargetEngage());
		TreeItem mouseOver = adapter.getMouseOver();
		if (mouseOver != null) {
			mouseOver.getWidget().removeStyleName(DragClientBundle.INSTANCE.css()
					.dropTargetEngage());
		}
	}

	@Override
	public void onMove(DragContext context) {
		// TODO: make a getMouseOverItem() method for Tree
//		dropTarget.getSelectedItem().getWidget().
	}

	@Override
	public void onPreviewDrop(DragContext context) throws VetoDragException {
	}
}

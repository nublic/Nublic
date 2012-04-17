package com.nublic.app.music.client.ui.dnd;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.nublic.app.music.client.ui.NavigationPanel;
import com.nublic.app.music.client.ui.TagWidget;
import com.nublic.app.music.client.ui.dnd.proxy.DragProxy;
import com.nublic.app.music.client.ui.dnd.proxy.HasProxy;
import com.nublic.app.music.client.ui.dnd.proxy.ProxyState;

public abstract class LeftDropController extends AbstractDropController {

	private NavigationPanel dropTarget;
	private TagWidget originalySelected;
	protected TagWidget overTag;
	DragProxy proxy = null;
	
	public LeftDropController(NavigationPanel dropTarget) {
		super(dropTarget);
		
		this.dropTarget = dropTarget;
	}
	
	@Override
	public void onEnter(DragContext context) {
		proxy = ((HasProxy) context.dragController).getProxy();
		originalySelected = dropTarget.getSelectedTag();
		setNewOverTag(dropTarget.getIntersectionTag(context.mouseX, context.mouseY));
		super.onEnter(context);
	}

	@Override
	public void onDrop(DragContext context) {

		if (overTag != null && overTag.getKind() != null) {
			// If it's defined and it's not "all music" tag
			switch (overTag.getKind()) {
			case COLLECTION:
				dropInCollection(context, overTag.getId());
				break;
			case PLAYLIST:
				dropInPlaylist(context, overTag.getId());
				break;
			}
		}

		super.onDrop(context);
	}
	
	public abstract void dropInCollection(DragContext context, String collectionId);
	public abstract void dropInPlaylist(DragContext context, String playlistId);
	
	@Override
	public void onMove(DragContext context) {
		setNewOverTag(dropTarget.getIntersectionTag(context.mouseX, context.mouseY));
		super.onMove(context);
	}

	private void setNewOverTag(TagWidget intersectionTag) {
		overTag = intersectionTag;
		if (overTag != null && overTag.getKind() != null) {
			dropTarget.select(intersectionTag);		// To give feedback on the drop panel
			proxy.setState(ProxyState.PLUS);		// To give feedback on the drag proxy
		} else {
			dropTarget.select(null);
			proxy.setState(ProxyState.NONE);
		}
	}

	@Override
	public void onLeave(DragContext context) {
		overTag = null;
		proxy.setState(ProxyState.NONE);
		proxy = null;
		dropTarget.select(originalySelected);
		super.onLeave(context);
	}

}

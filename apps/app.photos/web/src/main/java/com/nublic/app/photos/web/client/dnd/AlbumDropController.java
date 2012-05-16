package com.nublic.app.photos.web.client.dnd;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.nublic.app.photos.web.client.PhotosApp;
import com.nublic.app.photos.web.client.view.navigation.NavigationPanel;
import com.nublic.app.photos.web.client.view.navigation.TagWidget;
import com.nublic.util.gwt.LocationUtil;
import com.nublic.util.gwt.NublicLists;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;

public class AlbumDropController extends SimpleDropController {

	public final static String DROP_OVER_CSS_CLASS = "dropOver";

	private TagWidget widget;
	private DragProxy proxy;
	private NavigationPanel parentPanel;
	private long previouslySelected;

	public AlbumDropController(TagWidget w) {
		super(w);
		this.widget = w;
		parentPanel = PhotosApp.getUi().getNavigationPanel();
	}

	@Override
	public void onDrop(DragContext context) {
		if (widget.getAlbumId() == PhotosApp.getController().getCurrentAlbumId()) {
			// Cannot add to same album
			return;
		}
		
		String photoList = NublicLists.joinList(PhotosApp.getController().getSelectedPhotos(), ",");
		Message m = new Message() {
			@Override
			public String getURL() {
				return LocationUtil.encodeURL(GWT.getHostPageBaseURL() + "server/album/" + widget.getAlbumId());
			}
			@Override
			public void onSuccess(Response response) {
				// Do nothing
			}
			@Override
			public void onError() {
				// Do nothing
			}
		};
		m.addParam("photos", photoList);
		SequenceHelper.sendJustOne(m, RequestBuilder.PUT);
		super.onDrop(context);
	}

	@Override
	public void onEnter(DragContext context) {
		// Unselect previously selected
		previouslySelected = parentPanel.getSelectedId();
		parentPanel.unselectCollection();
		// Select mouse over one
		widget.select(true);
		// Get proxy and set "+" on it
		proxy = ((HasProxy) context.dragController).getProxy();
		proxy.setState(ProxyState.PLUS);
		super.onEnter(context);
	}

	@Override
	public void onLeave(DragContext context) {
		widget.select(false);
		parentPanel.selectCollection(previouslySelected);
		proxy.setState(ProxyState.NONE);
		super.onLeave(context);
	}

	@Override
	public void onPreviewDrop(DragContext context) throws VetoDragException {
		super.onPreviewDrop(context);
		if (widget.getAlbumId() < 0 || widget.getAlbumId() == PhotosApp.getController().getCurrentAlbumId()) {
			throw new VetoDragException();
		}
	}
}

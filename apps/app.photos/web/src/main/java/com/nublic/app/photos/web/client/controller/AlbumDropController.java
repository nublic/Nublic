package com.nublic.app.photos.web.client.controller;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.nublic.app.photos.web.client.PhotosApp;
import com.nublic.app.photos.web.client.view.navigation.TagWidget;
import com.nublic.util.gwt.LocationUtil;
import com.nublic.util.gwt.NublicLists;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;

public class AlbumDropController extends SimpleDropController {

	public final static String DROP_OVER_CSS_CLASS = "dropOver";

	private TagWidget widget;

	public AlbumDropController(TagWidget w) {
		super(w);
		this.widget = w;
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
		super.onEnter(context);
		widget.addStyleName(DROP_OVER_CSS_CLASS);
	}

	@Override
	public void onLeave(DragContext context) {
		widget.removeStyleName(DROP_OVER_CSS_CLASS);
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

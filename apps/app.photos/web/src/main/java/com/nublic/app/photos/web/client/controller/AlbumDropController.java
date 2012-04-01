package com.nublic.app.photos.web.client.controller;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.user.client.Window;
import com.nublic.app.photos.web.client.PhotosApp;
import com.nublic.app.photos.web.client.view.navigation.TagWidget;

public class AlbumDropController extends SimpleDropController {

	public final static String DROP_OVER_CSS_CLASS = "dropOver";

	private TagWidget widget;

	public AlbumDropController(TagWidget w) {
		super(w);
		this.widget = w;
	}

	@Override
	public void onDrop(DragContext context) {
		// TODO: send information about dropping
		Window.alert("dragged");
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

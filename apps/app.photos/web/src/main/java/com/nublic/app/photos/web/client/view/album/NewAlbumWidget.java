package com.nublic.app.photos.web.client.view.album;

import java.util.EnumSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.photos.web.client.controller.PhotosController;
import com.nublic.util.widgets.PopupButton;
import com.nublic.util.widgets.PopupButtonHandler;
import com.nublic.util.widgets.TextPopup;

public class NewAlbumWidget extends Composite implements HasMouseDownHandlers, ClickHandler {

	private static NewAlbumWidgetUiBinder uiBinder = GWT.create(NewAlbumWidgetUiBinder.class);
	
	interface NewAlbumWidgetUiBinder extends UiBinder<Widget, NewAlbumWidget> {
	}

	@UiField PushButton newButton;
	@UiField Anchor fileName;
	
	PhotosController controller;
	
	// path is the path of the folder where the file is placed
	public NewAlbumWidget(PhotosController controller) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.controller = controller;
		
		newButton.addClickHandler(this);
		fileName.addClickHandler(this);
	}

	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}
	
	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}
	
	public void addAlbum() {
		final TextPopup popup = new TextPopup("Add new album", 
				EnumSet.of(PopupButton.CANCEL, PopupButton.ADD), 
				PopupButton.ADD);
		popup.addButtonHandler(PopupButton.ADD, new PopupButtonHandler() {
			@Override
			public void onClicked(PopupButton button, ClickEvent event) {
				controller.onPutTag(popup.getText());
				popup.hide();
			}
		});
		popup.center();
		popup.selectAndFocus();
	}

	@Override
	public void onClick(ClickEvent arg0) {
		addAlbum();
	}

}

package com.nublic.app.photos.web.client.view.album;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.photos.web.client.Images;
import com.nublic.app.photos.web.client.controller.PhotosController;
import com.nublic.app.photos.web.client.model.CallbackOneAlbum;
import com.nublic.app.photos.web.client.model.PhotosModel;
import com.nublic.util.gwt.LocationUtil;
import com.nublic.util.widgets.AnchorPanel;

public class AlbumThumbnailWidget extends Composite implements HasMouseDownHandlers {

	private static AlbumThumbnailWidgetUiBinder uiBinder = GWT.create(AlbumThumbnailWidgetUiBinder.class);
	
	interface AlbumThumbnailWidgetUiBinder extends UiBinder<Widget, AlbumThumbnailWidget> {
	}
	
	// CSS Styles defined in the .xml file
	interface Style extends CssResource {
		String inLine();
	    String maxheight();
	    String ellipcenter();
	    String shadowed();
	}

	@UiField AnchorPanel imagePanel;
	@UiField Image image;
	@UiField Hyperlink fileName;
	@UiField Style style;
	// @UiField CheckBox selectedBox;
	// @UiField PushButton playButton;
	
	PhotosController controller;
	long id;
	String name;
	
	// path is the path of the folder where the file is placed
	public AlbumThumbnailWidget(PhotosController controller, long id, String name) {
		initWidget(uiBinder.createAndBindUi(this));
		
		image.addErrorHandler(new ErrorHandler() {
			@Override
			public void onError(ErrorEvent e) {
				image.setResource(Images.INSTANCE.emptyAlbum());
			}
		});
		
		this.controller = controller;
		this.id = id;
		this.name = name;
	}
	
	public void lazyLoad() {
		PhotosModel.get().album(id, new CallbackOneAlbum() {
			
			@Override
			public void list(long id, String name) {				
				// Set up image
				String imageUrl = LocationUtil.encodeURL(GWT.getHostPageBaseURL() + "server/random/" + id + ".png");
				image.setUrl(imageUrl);
				
				// Set up name
				fileName.setText(name);
				fileName.setTitle(name);
				
				// Set up targets
				String target = "album=" + id + "&view=cells";
				fileName.setTargetHistoryToken(target);
				imagePanel.setHref("#" + target);
			}
			
			@Override
			public void error() {
				// Do nothing
			}
		});
	}
	
	public String getName() {
		return this.name;
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

}

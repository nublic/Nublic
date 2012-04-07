package com.nublic.app.photos.web.client.view.album;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.photos.web.client.controller.PhotosController;
import com.nublic.app.photos.web.client.model.CallbackOneAlbum;
import com.nublic.app.photos.web.client.model.PhotosModel;
import com.nublic.util.gwt.LocationUtil;

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
	    String childForHoverNotSelected();
	    String childForHoverSelected();
	}

	@UiField VerticalPanel imagePanel;
	@UiField VerticalPanel textPanel;
	@UiField Style style;
	// @UiField CheckBox selectedBox;
	// @UiField PushButton playButton;
	
	PhotosController controller;
	long id;
	String name;
	
	boolean initialized = false;
	Element divImage;
	Hyperlink fileName;
	
	// path is the path of the folder where the file is placed
	public AlbumThumbnailWidget(PhotosController controller, long id, String name) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.controller = controller;
		this.id = id;
		this.name = name;
	}
	
	public void lazyLoad() {
		PhotosModel.get().album(id, new CallbackOneAlbum() {
			
			@Override
			public void list(long id, String name) {
				if (!initialized) {
					// Set the thumbnail
					divImage = DOM.createDiv();
					
					// Create the widgets
					Hyperlink fileThumbnail = new Hyperlink();
					fileName = new Hyperlink();
					
					// Associate CSS styles
					fileThumbnail.getElement().addClassName(style.maxheight());
					fileName.getElement().addClassName(style.ellipcenter());
					
					// Add image
					fileThumbnail.getElement().getChild(0).appendChild(divImage);
					
					// Set up target
					String target = "album=" + id + "&view=cells";
					fileThumbnail.setTargetHistoryToken(target);
					fileName.setTargetHistoryToken(target);
					
					// Add the widgets to the panels
					imagePanel.add(fileThumbnail);
					textPanel.add(fileName);
					
					initialized = true;
				}
				
				// Set up image
				String imageUrl = LocationUtil.encodeURL(GWT.getHostPageBaseURL() + "server/random/" + id + ".png");
				divImage.setAttribute("style", "height: 96px; width: 96px; background-image: url('" + imageUrl + "');" +
						" background-position: center center; background-repeat: no-repeat;");
				
				// Set up name
				fileName.setText(name);
				fileName.setTitle(name);
			}
			
			@Override
			public void error() {
				// Do nothing
			}
		});
	}

	
	/* @UiHandler("playButton")
	void onPlayButtonMouseDown(MouseDownEvent event) {
		// Do nothing by now
	} */
	
	// No selection methods by now
	
	/* public boolean isChecked() {
		return selectedBox.getValue();
	}
	
	public void setChecked(boolean checked) {
		// selectedBox.setValue(checked);
		selectedBox.setValue(checked, true);
	}*/

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

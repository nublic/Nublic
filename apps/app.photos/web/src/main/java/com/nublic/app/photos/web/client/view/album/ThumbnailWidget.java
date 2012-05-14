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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.photos.web.client.controller.PhotosController;
import com.nublic.app.photos.web.client.model.AlbumInfo;
import com.nublic.app.photos.web.client.model.CallbackOnePhoto;
import com.nublic.app.photos.web.client.model.PhotoInfo;
import com.nublic.app.photos.web.client.model.PhotosModel;
import com.nublic.util.gwt.LocationUtil;

public class ThumbnailWidget extends Composite implements HasMouseDownHandlers {

	private static FileWidgetUiBinder uiBinder = GWT.create(FileWidgetUiBinder.class);
	
	interface FileWidgetUiBinder extends UiBinder<Widget, ThumbnailWidget> {
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
	@UiField CheckBox selectedBox;
	// @UiField PushButton playButton;
	
	PhotosController controller;
	AlbumInfo album;
	long photoPosition;
	
	boolean initialized = false;
	Element divImage;
	Hyperlink fileName;
	
	// path is the path of the folder where the file is placed
	public ThumbnailWidget(PhotosController controller, AlbumInfo album, long photoPosition) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.controller = controller;
		this.album = album;
		this.photoPosition = photoPosition;
	}
	
	public void lazyLoad() {
		PhotosModel.get().photo(photoPosition, new CallbackOnePhoto() {
			
			@Override
			public void list(final AlbumInfo info, final PhotoInfo photo) {
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
					String target = "album=" + info.getId() + "&view=presentation&photo=" + photoPosition;
					fileThumbnail.setTargetHistoryToken(target);
					fileName.setTargetHistoryToken(target);
					
					// Add the widgets to the panels
					imagePanel.add(fileThumbnail);
					textPanel.add(fileName);
					
					// Set up selected boxes
					selectedBox.setValue(false, false);
					selectedBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
						@Override
						public void onValueChange(ValueChangeEvent<Boolean> event) {
							// Set CSS style
							if (event.getValue()) {
								selectedBox.removeStyleName(style.childForHoverNotSelected());
								selectedBox.addStyleName(style.childForHoverSelected());
								controller.select(photo);
							} else {
								selectedBox.removeStyleName(style.childForHoverSelected());
								selectedBox.addStyleName(style.childForHoverNotSelected());
								controller.unselect(photo);
							}
						}
					});
					
					initialized = true;
				}
				
				// Set up image
				String imageUrl = LocationUtil.encodeURL(GWT.getHostPageBaseURL() + "server/thumbnail/" + photo.getId() + ".png");
				divImage.setAttribute("style", "height: 96px; width: 96px; background-image: url('" + imageUrl + "');" +
						" background-position: center center; background-repeat: no-repeat;");
				
				// Set up name
				fileName.setText(photo.getTitle());
				fileName.setTitle(photo.getTitle());
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
	
	public boolean isChecked() {
		return selectedBox.getValue();
	}
	
	public void setChecked(boolean checked) {
		// selectedBox.setValue(checked);
		selectedBox.setValue(checked, true);
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

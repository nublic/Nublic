package com.nublic.app.photos.web.client.view.album;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.photos.common.model.AlbumInfo;
import com.nublic.app.photos.common.model.CallbackOnePhoto;
import com.nublic.app.photos.common.model.PhotoInfo;
import com.nublic.app.photos.common.model.PhotosModel;
import com.nublic.app.photos.web.client.controller.PhotosController;
import com.nublic.util.gwt.LocationUtil;
import com.nublic.util.widgets.AnchorPanel;

public class ThumbnailWidget extends Composite implements HasMouseDownHandlers {

	private static FileWidgetUiBinder uiBinder = GWT.create(FileWidgetUiBinder.class);
	
	interface FileWidgetUiBinder extends UiBinder<Widget, ThumbnailWidget> {
	}
	
	// CSS Styles defined in the .xml file
	interface Style extends CssResource {
		String inLine();
	    String maxmeasures();
	    String ellipcenter();
	    String shadowed();
	    String childForHoverNotSelected();
	    String childForHoverSelected();
	}

	@UiField AnchorPanel anchorPanel;
	@UiField Image fileThumbnail;
	@UiField Hyperlink fileName;
	@UiField Style style;
	@UiField CheckBox selectedBox;
	
	PhotosController controller;
	AlbumInfo album;
	long photoPosition;
	
	boolean initialized = false;	

	public ThumbnailWidget(PhotosController controller, AlbumInfo album, long photoPosition) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.controller = controller;
		this.album = album;
		this.photoPosition = photoPosition;
	}
	
	public void lazyLoad() {
		PhotosModel.get().photo(photoPosition, new CallbackOnePhoto() {	
			@Override
			public void list(AlbumInfo info, PhotoInfo photo) {
				if (!initialized) {
					setTarget(info);
					setBoxChangeHandling(photo);
					initialized = true;
				}

				// Set image
				fileThumbnail.setUrl(LocationUtil.encodeURL(GWT.getHostPageBaseURL() + "server/thumbnail/" + photo.getId() + ".png"));

				// Set up name
				fileName.setText(photo.getTitle());
				fileName.setTitle(photo.getTitle());
				fileThumbnail.setTitle(photo.getTitle());
			}

			@Override
			public void error() {
				// Do nothing
			}
		});
	}
	
	private void setTarget(AlbumInfo info) {
		// Set up target
		final String target = "album=" + info.getId() + "&view=presentation&photo=" + photoPosition;
		fileName.setTargetHistoryToken(target);
		anchorPanel.setHref("#" + target);
	}
	
	private void setBoxChangeHandling(final PhotoInfo photo) {
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
	}

	public boolean isChecked() {
		return selectedBox.getValue();
	}
	
	public void setChecked(boolean checked) {
		selectedBox.setValue(checked, true);
	}
	
	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}

}

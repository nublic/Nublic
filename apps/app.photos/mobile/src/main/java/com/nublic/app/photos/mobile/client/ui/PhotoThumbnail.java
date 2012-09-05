package com.nublic.app.photos.mobile.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.photos.common.model.PhotoInfo;
import com.nublic.util.gwt.LocationUtil;

public class PhotoThumbnail extends Composite {
	private static PhotoThumbnailUiBinder uiBinder = GWT.create(PhotoThumbnailUiBinder.class);
	interface PhotoThumbnailUiBinder extends UiBinder<Widget, PhotoThumbnail> {}

	@UiField Image thumbnail;
	PhotoInfo photo;
	AlbumGrid parentPage;
	int photoIndex;
	
	public PhotoThumbnail(PhotoInfo pi, AlbumGrid albumGrid, int photoIndex) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.photo = pi;
		this.parentPage = albumGrid;
		this.photoIndex = photoIndex;
		load();
	}
	
	public void load() {
		// Set image
		thumbnail.setUrl(LocationUtil.encodeURL(GWT.getHostPageBaseURL() + "server/thumbnail/" + photo.getId() + ".png"));

		// Set up name
//		fileName.setText(photo.getTitle());
//		fileName.setTitle(photo.getTitle());
//		fileThumbnail.setTitle(photo.getTitle());
		
		addClickHandler();
	}

	private void addClickHandler() {
		this.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				parentPage.goTo(new PhotoView(photoIndex));
			}
		}, ClickEvent.getType());
	}

}

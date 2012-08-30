package com.nublic.app.photos.mobile.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.photos.common.model.PhotoInfo;

public class PhotoThumbnail extends Composite {
	private static PhotoThumbnailUiBinder uiBinder = GWT.create(PhotoThumbnailUiBinder.class);
	interface PhotoThumbnailUiBinder extends UiBinder<Widget, PhotoThumbnail> {}

	public PhotoThumbnail(PhotoInfo pi) {
		initWidget(uiBinder.createAndBindUi(this));
		
		// TODO ...
	}

}

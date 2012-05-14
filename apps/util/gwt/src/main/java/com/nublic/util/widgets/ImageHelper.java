package com.nublic.util.widgets;

import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

public class ImageHelper {
	
	// Sets the image in imgURL as the image of imgTarget
	// if it fails, it uses alternative ImageResource to fill imgTarget
	public static void setImage(final Image imgTarget, String imgURL, final ImageResource alternative) {
		imgTarget.addErrorHandler(new ErrorHandler() {
			@Override
			public void onError(ErrorEvent event) {
				ImageHelper.setImage(imgTarget, alternative);
			}
		});
		imgTarget.setUrl(imgURL);
	}
	
	public static void setImage(Image target, ImageResource source) {
		String style = target.getStyleName();
		target.setResource(source);
		target.addStyleName(style);
	}
}

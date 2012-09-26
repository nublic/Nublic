package com.nublic.app.photos.mobile.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ImageInSlide extends Composite {
	private static ImageInSlideUiBinder uiBinder = GWT.create(ImageInSlideUiBinder.class);
	interface ImageInSlideUiBinder extends UiBinder<Widget, ImageInSlide> {	}

	@UiField VerticalPanel imagePanel;
	@UiField Image loading;
	
	public ImageInSlide(Image image) {
		initWidget(uiBinder.createAndBindUi(this));
		
		image.addLoadHandler(new LoadHandler() {
			@Override
			public void onLoad(LoadEvent event) {
				loading.setVisible(false);
			}
		});
		
		imagePanel.add(image);
	}

}

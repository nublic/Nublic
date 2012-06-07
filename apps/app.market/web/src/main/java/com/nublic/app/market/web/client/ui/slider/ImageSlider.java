package com.nublic.app.market.web.client.ui.slider;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;

public class ImageSlider extends Composite {
	private static ImageSliderUiBinder uiBinder = GWT.create(ImageSliderUiBinder.class);
	interface ImageSliderUiBinder extends UiBinder<Widget, ImageSlider> {}

	@UiField Image prevButton;
	@UiField Image nextButton;
	@UiField Image showingImage;

	List<Image> imageList = new ArrayList<Image>();
	int currentIndex = -1;

	public ImageSlider() {
		initWidget(uiBinder.createAndBindUi(this));
//		this.setVisible(false);
	}
	
	public void addImage(String url) {
		imageList.add(new Image(url));
		if (imageList.size() == 1) {
			// It's the first image
			currentIndex = 0;
			show(0);
		}
	}
	
	private void show(Image img) {
		this.setVisible(true);
		showingImage.setUrl(img.getUrl());
	}
	
	private void show(int index) {
		show(imageList.get(index));
	}
	
	public void showNext() {
		currentIndex = currentIndex + 1 >= imageList.size() ? 0 : currentIndex + 1;
		show(currentIndex);
	}

	public void showPrev() {
		currentIndex = currentIndex - 1 < 0 ? imageList.size() - 1 : currentIndex - 1;
		show(currentIndex);
	}

	@UiHandler("prevButton")
	void onPrevButtonClick(ClickEvent event) {
		showPrev();
	}

	@UiHandler("nextButton")
	void onNextButtonClick(ClickEvent event) {
		showNext();
	}
}

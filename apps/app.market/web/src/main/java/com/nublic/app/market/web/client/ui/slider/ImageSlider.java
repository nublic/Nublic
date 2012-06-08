package com.nublic.app.market.web.client.ui.slider;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ImageSlider extends Composite {
	private static ImageSliderUiBinder uiBinder = GWT.create(ImageSliderUiBinder.class);
	interface ImageSliderUiBinder extends UiBinder<Widget, ImageSlider> {}

	@UiField Image prevButton;
	@UiField Image nextButton;
	@UiField Image showingImage;
	@UiField Image transitionImage;
	
	@UiField SliderStyle style;

	public interface SliderStyle extends CssResource {
		String mainPanelBackground();
		String prevAndNext();
		String imageLimits();
		String imageShadow();
		String imagePanel();
		String transform();
		String positionLeft();
		String positionRight();
	}
	
	List<Image> imageList = new ArrayList<Image>();
	int currentIndex = -1;

	public ImageSlider() {
		initWidget(uiBinder.createAndBindUi(this));
		this.setVisible(false);
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
	
	private void showTransition(Image img) {
		transitionImage.setUrl(img.getUrl());
	}
	
	private void showTransition(int index) {
		showTransition(imageList.get(index));
	}

	public void showNext() {
		showTransition(currentIndex);
		currentIndex = currentIndex + 1 >= imageList.size() ? 0 : currentIndex + 1;
		show(currentIndex);
		animateRight();
	}

	public void showPrev() {
		showTransition(currentIndex);
		currentIndex = currentIndex - 1 < 0 ? imageList.size() - 1 : currentIndex - 1;
		show(currentIndex);
		animateLeft();
	}

	@UiHandler("prevButton")
	void onPrevButtonClick(ClickEvent event) {
		showPrev();
	}

	@UiHandler("nextButton")
	void onNextButtonClick(ClickEvent event) {
		showNext();
	}
	
	public void animateRight() {
		// Remove transform, so they don't get animated
		showingImage.removeStyleName(style.transform());
		transitionImage.removeStyleName(style.transform());
		// Put initial state of animation
		showingImage.addStyleName(style.positionLeft());
		transitionImage.removeStyleName(style.positionRight());
		transitionImage.removeStyleName(style.positionLeft());
		Timer t = new Timer() {
			@Override
			public void run() {
				// Put transform and final state of animation
				showingImage.addStyleName(style.transform());
				transitionImage.addStyleName(style.transform());
				showingImage.removeStyleName(style.positionLeft());
				transitionImage.addStyleName(style.positionRight());
			}
		};
		t.schedule(50);
	}
	
	public void animateLeft() {
		// Remove transform, so they don't get animated
		showingImage.removeStyleName(style.transform());
		transitionImage.removeStyleName(style.transform());
		// Put initial state of animation
		showingImage.addStyleName(style.positionRight());
		transitionImage.removeStyleName(style.positionRight());
		transitionImage.removeStyleName(style.positionLeft());
		Timer t = new Timer() {
			@Override
			public void run() {
				// Put transform and final state of animation
				showingImage.addStyleName(style.transform());
				transitionImage.addStyleName(style.transform());
				showingImage.removeStyleName(style.positionRight());
				transitionImage.addStyleName(style.positionLeft());
			}
		};
		t.schedule(50);
	}	
	
}

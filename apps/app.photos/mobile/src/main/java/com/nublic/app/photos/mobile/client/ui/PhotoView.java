package com.nublic.app.photos.mobile.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.gwtmobile.ui.client.page.Page;
import com.gwtmobile.ui.client.widgets.HeaderPanel;
import com.gwtmobile.ui.client.widgets.Slide;
import com.gwtmobile.ui.client.widgets.SlidePanel;
import com.gwtmobile.ui.client.widgets.SlidePanel.SlideProvider;
import com.nublic.app.photos.common.model.AlbumInfo;
import com.nublic.app.photos.common.model.CallbackRowCount;
import com.nublic.app.photos.common.model.CallbackThreePhotos;
import com.nublic.app.photos.common.model.PhotoInfo;
import com.nublic.app.photos.common.model.PhotosModel;
import com.nublic.util.gwt.LocationUtil;

public class PhotoView extends Page implements SlideProvider {
	private static PhotoViewUiBinder uiBinder = GWT.create(PhotoViewUiBinder.class);
	interface PhotoViewUiBinder extends UiBinder<Widget, PhotoView> { }

	@UiField HeaderPanel header;
	@UiField SlidePanel slider;
	
	int rowCount;
	Image[] imageArray;

	public PhotoView(final int photoIndex) {
		initWidget(uiBinder.createAndBindUi(this));		
		
		PhotosModel.get().rowCount(new CallbackRowCount() {
			@Override
			public void rowCount(AlbumInfo info, long _rowCount) {
				rowCount = (int) _rowCount;
				slider.setSlideCount(rowCount);
				imageArray = new Image[rowCount];
				slider.setSlideProvider(PhotoView.this);
				slider.getSlideProvider().loadSlide(photoIndex);
			}
			@Override
			public void error() {
				// nothing				
			}
		});

//		header.setLeftButtonClickHandler(new ClickHandler() {			
//			@Override
//			public void onClick(ClickEvent event) {
//				if (slider.getCurrentSlideIndex() > 0) {
//					slider.previous();
//				}
//				else {
//					goBack(null);
//				}
//			}
//		});

//		header.setRightButtonClickHandler(new ClickHandler() {			
//			@Override
//			public void onClick(ClickEvent event) {
//				slider.next();
//			}
//		});
	}

	@Override
	public Slide loadSlide(final int index) {
		if (imageArray == null) {
			return null;
		} else {
			Slide slide = new Slide();
			slide.addStyleName("Slide-Content");
			
			if (imageArray[index] == null) {
				imageArray[index] = new Image();
			}
	
			PhotosModel.get().photosAround(index, new CallbackThreePhotos() {
				@Override
				public void list(AlbumInfo info, PhotoInfo prev, PhotoInfo current, PhotoInfo next) {
					if (index - 1 >= 0) {
						setArrayImage(index -1, prev);
					}
					setArrayImage(index, current);
					if (index + 1 < rowCount) {
						setArrayImage(index +1, next);
					}
				}
				@Override
				public void error() {
					// nothing
				}
			});
	
			slide.add(imageArray[index]);
			slide.add(new HTML("Photo " + index));
			return slide;
		}
	}
	
	private void setArrayImage(int index, PhotoInfo photo) {
		if (imageArray[index] == null) {
			imageArray[index] = new Image();
		}
		imageArray[index].setUrl(LocationUtil.encodeURL(GWT.getHostPageBaseURL() + "server/view/" + photo.getId() + ".png"));
	}
	
	

}

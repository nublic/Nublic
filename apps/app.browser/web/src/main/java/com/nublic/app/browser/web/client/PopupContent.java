package com.nublic.app.browser.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PopupContent extends Composite {

	private static PopupContentUiBinder uiBinder = GWT.create(PopupContentUiBinder.class);

	interface PopupContentUiBinder extends UiBinder<Widget, PopupContent> {
	}

	@UiField VerticalPanel contentTop;
	Widget internalWidget;
	// Used to proper resize images
	int originalWidth;
	int originalHeight;

	public PopupContent() {
		initWidget(uiBinder.createAndBindUi(this));
		internalWidget = null;
		originalWidth = 0;
		originalHeight = 0;
	}
	
	public void setContent(Widget w) {
		internalWidget = w;
		contentTop.clear();
		contentTop.add(w);
		if (w instanceof Image) {
			originalWidth = ((Image)w).getWidth();
			originalHeight = ((Image)w).getHeight();
		} else {
			originalWidth = 0;
			originalHeight = 0;
		}
	}
	
	public Widget getWidget() {
		return internalWidget;
	}

	public void fitSize(int width, int height) {
		if (internalWidget != null) {
			if (internalWidget instanceof Image) {
				if (originalWidth > 0 && width > 0 && originalHeight > 0 && height > 0) {
					// to avoid 0 divisions and setting negative lengths
					float widgetRatio = originalHeight / (float) originalWidth;
					float boxRatio = height / (float) width;
	
					if (widgetRatio < boxRatio) {
						// the widget is flatter than the box
						if (originalWidth > width) {
							// its original image overflows the width
							internalWidget.setPixelSize(width, (int) (width * widgetRatio));
						} else {
							internalWidget.setPixelSize(originalWidth, originalHeight);
						}
					} else {
						// the widget is more stretched than the box
						if (originalHeight > height) {
							// its original image overflows the height
							internalWidget.setPixelSize((int) (height / widgetRatio), height);
						} else {
							internalWidget.setPixelSize(originalWidth, originalHeight);
						}
					}
				} else {
					// some of the widths are 0
					internalWidget.setPixelSize(0, 0);
				}
			} else {
				// If it's not an Image
				internalWidget.setPixelSize(width, height);
			}
		}
		
	}
	

}

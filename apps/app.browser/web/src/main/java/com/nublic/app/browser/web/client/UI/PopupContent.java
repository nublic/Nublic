package com.nublic.app.browser.web.client.UI;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.Constants;

public class PopupContent extends Composite {

	private static PopupContentUiBinder uiBinder = GWT.create(PopupContentUiBinder.class);

	interface PopupContentUiBinder extends UiBinder<Widget, PopupContent> {
	}

	@UiField LayoutPanel contentTop;
	@UiField Anchor nextLink;
	@UiField Anchor previousLink;
	Widget internalWidget;
	// Used to proper resize images
	int originalWidth;
	int originalHeight;
	FileWidget previous;
	FileWidget next;

	public PopupContent() {
		initWidget(uiBinder.createAndBindUi(this));
		internalWidget = null;
		originalWidth = 0;
		originalHeight = 0;
	}
	
	public void setPrevious(FileWidget previous) {
		this.previous = previous;
	}

	public void setNext(FileWidget next) {
		this.next = next;
	}
	
	@UiHandler("nextLink")
	void onNextLinkClick(ClickEvent event) {
		if (next != null) {
			History.newItem(Constants.getView(next.getViewType()) + "?" + Constants.PATH_PARAMETER + "=" + next.getPath());
		}
	}

	@UiHandler("previousLink")
	void onPreviousLinkClick(ClickEvent event) {
		if (previous != null) {
			History.newItem(Constants.getView(previous.getViewType()) + "?" + Constants.PATH_PARAMETER + "=" + previous.getPath());
		}
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
	
	public Widget getContent() {
		return internalWidget;
	}

	public void fitSize(int width, int height) {
		if (internalWidget != null) {
			if (internalWidget instanceof Image) {
				// Images
				if (originalWidth > 0 && width > 0 && originalHeight > 0 && height > 0) {
					// to avoid 0 divisions and setting negative lengths
					float widgetRatio = originalHeight / (float) originalWidth;
					float boxRatio = height / (float) width;
	
					if (widgetRatio < boxRatio) {
						// the widget is flatter than the box
						if (originalWidth > width) {
							// its original image overflows the width
							int newHeight = (int) (width * widgetRatio);
							internalWidget.setPixelSize(width, newHeight);
							contentTop.setWidgetLeftWidth(internalWidget, 0, Unit.PX, width, Unit.PX);
							contentTop.setWidgetTopHeight(internalWidget, (int) ((height - newHeight) / 2), Unit.PX,
																		  newHeight, Unit.PX);
						} else {
							internalWidget.setPixelSize(originalWidth, originalHeight);
							contentTop.setWidgetLeftWidth(internalWidget, (int) ((width - originalWidth) / 2), Unit.PX, width, Unit.PX);
							contentTop.setWidgetTopHeight(internalWidget, (int) ((height - originalHeight) / 2), Unit.PX, height, Unit.PX);
						}
					} else {
						// the widget is more stretched than the box
						if (originalHeight > height) {
							// its original image overflows the height
							int newWidth = (int) (height / widgetRatio);
							internalWidget.setPixelSize(newWidth, height);
							contentTop.setWidgetLeftWidth(internalWidget, (int) ((width - newWidth) / 2), Unit.PX,
																		  newWidth, Unit.PX);
							contentTop.setWidgetTopHeight(internalWidget, 0, Unit.PX, height, Unit.PX);
						} else {
//							internalWidget.setPixelSize(originalWidth, originalHeight);
							contentTop.setWidgetLeftWidth(internalWidget, (int) ((width - originalWidth) / 2), Unit.PX, width, Unit.PX);
							contentTop.setWidgetTopHeight(internalWidget, (int) ((height - originalHeight) / 2), Unit.PX, height, Unit.PX);
						}
					}
				} else {
					// some of the widths are 0, set the image size to 0, but not if the original sizes are not set yet
					if (originalHeight != 0 && originalHeight != 0) {
						internalWidget.setPixelSize(0, 0);
					}
				}
//			} else if (internalWidget instanceof AbstractMediaPlayer) {
//				// Music and video
			} else {
				// If it's not an Image we'll fill the space we have for it
				internalWidget.setPixelSize(width, height);
			}
		}
		
	}

	public void setOriginalSize(int width, int height) {
		originalWidth = width;
		originalHeight = height;
	}

}

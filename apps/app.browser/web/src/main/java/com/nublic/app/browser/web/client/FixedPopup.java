package com.nublic.app.browser.web.client;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

// A pop-up that occupy all the space except a margin at boundaries
public class FixedPopup extends PopupPanel implements ResizeHandler {
	int margin;
	PopupContent content;

	public FixedPopup(boolean autoHide, boolean modal) {
		this(autoHide, modal, Constants.POPUP_MARGIN);
	}
	
	public FixedPopup(boolean autoHide, boolean modal, int margin) {
		super(autoHide, modal);
		this.margin = margin;
		
		Window.addResizeHandler(this);
		content = new PopupContent();
		this.add(content);
	}
	
	public int getMargin() {
		return margin;
	}

	public void setMargin(int margin) {
		this.margin = margin;
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		resize();
	}

	@Override
	public void onResize(ResizeEvent event) {
		resize(event.getWidth(), event.getHeight());
	}
	
	public void resize() {
		resize(Window.getClientWidth(), Window.getClientHeight());
	}
	
	// Receives the size of the window where it is placed
	public void resize(int newWidth, int newHeight) {
		int width = newWidth - 2*margin;
		int height = newHeight - 2*margin;
		if (width < 0) {
			width = 0;
		}
		if (height < 0) {
			height = 0;
		}
		this.setSize("" + width + "px", "" + height + "px");
		this.setPopupPosition(margin, margin);
		
		content.fitSize(width, height - Constants.POPUP_BOTTOM);
	}

	public void setContentWidget(Widget w) {
		if (content != null) {
			content.setContent(w);
		}
	}
	
}

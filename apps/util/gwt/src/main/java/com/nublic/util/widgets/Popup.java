package com.nublic.util.widgets;

import java.util.EnumSet;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class Popup extends PopupPanel implements ResizeHandler {
	PopupContent content;
	
	public PopupButtonHandler POPUP_CLOSE = new PopupButtonHandler() {
		@Override
		public void onClicked(PopupButton button, ClickEvent event) {
			Popup.this.hide(true);
		}
	};

	public Popup(String title, EnumSet<PopupButton> buttonsToShow, Widget w, String customLabel) {
		super(false, true);
		this.setGlassEnabled(true);
		
		Window.addResizeHandler(this);
		content = new PopupContent(title, buttonsToShow, w, customLabel);
		this.add(content);
	}
	
	public Popup(String title, EnumSet<PopupButton> buttonsToShow, Widget w) {
		this(title, buttonsToShow, w, null);
	}
	
	public void addButtonHandler(PopupButton button, PopupButtonHandler handler) {
		content.addButtonHandler(button, handler);
	}
	
	public void setInnerHeight(String h) {
		this.content.setInnerHeight(h);
	}
	
	@Override
	public void onResize(ResizeEvent event) {
		if (isShowing()) {
			super.center();
		}
	}

}

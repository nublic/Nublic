package com.nublic.util.widgets;

import java.util.EnumSet;

import com.google.gwt.user.client.ui.TextBox;

public class TextPopup extends Popup {
	
	TextBox box;

	public TextPopup(String title, EnumSet<PopupButton> buttonsToShow, String customLabel) {
		super(title, buttonsToShow, null, customLabel);
		
		box = new TextBox();
		box.setWidth("100%");
		box.setHeight("100%");
		this.addWidget(box);
		
		setInnerHeight(130);
		addButtonHandler(PopupButton.CLOSE, POPUP_CLOSE);
		addButtonHandler(PopupButton.CANCEL, POPUP_CLOSE);
	}
	
	public TextPopup(String title, EnumSet<PopupButton> buttonsToShow) {
		this(title, buttonsToShow, null);
	}
	
	public TextPopup(String title) {
		this(title, EnumSet.of(PopupButton.OK, PopupButton.CANCEL));
	}
	
	public String getText() {
		return box.getText();
	}
	
	public void setText(String text) {
		box.setText(text);
	}
}

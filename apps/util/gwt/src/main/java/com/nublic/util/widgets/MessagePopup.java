package com.nublic.util.widgets;

import java.util.EnumSet;

import com.google.gwt.user.client.ui.Label;

public class MessagePopup extends Popup {

	public MessagePopup(String title, String message, EnumSet<PopupButton> buttonsToShow, String customLabel) {
		super(title, buttonsToShow, new Label(message), customLabel);
		
		setInnerHeight(120);
		addButtonHandler(PopupButton.CLOSE, POPUP_CLOSE);
		addButtonHandler(PopupButton.CANCEL, POPUP_CLOSE);
	}
	
	public MessagePopup(String title, String message, EnumSet<PopupButton> buttonsToShow) {
		this(title, message, buttonsToShow, null);
	}
	
	public MessagePopup(String title, String message) {
		this(title, message, EnumSet.of(PopupButton.OK, PopupButton.CANCEL));
	}
}

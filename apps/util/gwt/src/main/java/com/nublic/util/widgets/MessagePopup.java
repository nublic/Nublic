package com.nublic.util.widgets;

import java.util.EnumSet;

public class MessagePopup extends Popup {

	public MessagePopup(String title, String message, EnumSet<PopupButton> buttonsToShow, String customLabel) {
		super(title, buttonsToShow, new InfoFill(message), customLabel);
	}
	
	public MessagePopup(String title, String message, EnumSet<PopupButton> buttonsToShow) {
		this(title, message, buttonsToShow, null);
	}
	
	public MessagePopup(String title, String message) {
		this(title, message, EnumSet.of(PopupButton.OK, PopupButton.CANCEL));
	}
}

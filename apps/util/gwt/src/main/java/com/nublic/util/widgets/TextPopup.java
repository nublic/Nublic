package com.nublic.util.widgets;

import java.util.EnumSet;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.TextBox;

public class TextPopup extends Popup {
	
	TextBox box;

	public TextPopup(String title, EnumSet<PopupButton> buttonsToShow, String customLabel, final PopupButton defaultButton) {
		super(title, buttonsToShow, null, customLabel);
		
		box = new TextBox();
		box.setWidth("100%");
		box.setHeight("100%");
		this.addWidget(box);
		
		box.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					event.preventDefault();
					TextPopup.this.content.handle(defaultButton, null);
				}
			}
		});
		
		setInnerHeight(130);
		addButtonHandler(PopupButton.CLOSE, POPUP_CLOSE);
		addButtonHandler(PopupButton.CANCEL, POPUP_CLOSE);
	}
	
	public TextPopup(String title, EnumSet<PopupButton> buttonsToShow, final PopupButton defaultButton) {
		this(title, buttonsToShow, null, defaultButton);
	}
	
	public TextPopup(String title) {
		this(title, EnumSet.of(PopupButton.OK, PopupButton.CANCEL), PopupButton.OK);
	}
	
	public String getText() {
		return box.getText();
	}
	
	public void setText(String text) {
		box.setText(text);
	}
}

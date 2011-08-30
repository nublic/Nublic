package com.nublic.app.example.web.client;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

public class ErrorPopup extends PopupPanel {

	public ErrorPopup(String message) {
		super(true); // Set 'auto-hide' to true
		setWidget(new Label(message)); // Add the message
		setGlassEnabled(true); // Shadow background widgets
	}
}

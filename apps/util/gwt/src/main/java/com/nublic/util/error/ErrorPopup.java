package com.nublic.util.error;

import java.util.EnumSet;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.util.i18n.Constants;
import com.nublic.util.widgets.Popup;
import com.nublic.util.widgets.PopupButton;

public class ErrorPopup extends Popup {

	public ErrorPopup(String message) {
		super(Constants.I18N.error(), EnumSet.of(PopupButton.OK), new Label(message));
		setCommonProperties();
	}
	
	public ErrorPopup(Widget w) {
		super(Constants.I18N.error(), EnumSet.of(PopupButton.OK), w);
		setCommonProperties();
	}
	
	private void setCommonProperties() {
		setInnerHeight(120);
		addButtonHandler(PopupButton.CLOSE, POPUP_CLOSE);
		addButtonHandler(PopupButton.OK, POPUP_CLOSE);
	}
	
	public static void showError(String message) {
		ErrorPopup ep = new ErrorPopup(message);
		ep.center();
	}
	
	public static void showError(Widget w) {
		ErrorPopup ep = new ErrorPopup(w);
		ep.center();
	}
}

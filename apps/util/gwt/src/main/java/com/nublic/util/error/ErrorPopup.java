package com.nublic.util.error;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class ErrorPopup extends PopupPanel implements HasText {
	ErrorContent content;

	public ErrorPopup(String message) {
		content = new ErrorContent(message, this);
		this.add(content);
		setStyle();
	}

	public ErrorPopup(Widget w) {
		content = new ErrorContent(this);
		content.setInternalWidget(w);
		this.add(content);
		setStyle();
	}
	
	private void setStyle() {
		this.setGlassEnabled(true);
		this.setModal(true);
		this.setAutoHideEnabled(true);
	}

	public static void showError(String message) {
		ErrorPopup error = new ErrorPopup(message);
		error.center();
	}
	
	public static void showError(Widget w) {
		ErrorPopup error = new ErrorPopup(w);
		error.center();
	}

	@Override
	public String getText() {
		return content.getText();
	}

	@Override
	public void setText(String text) {
		content.setText(text);
	}

}

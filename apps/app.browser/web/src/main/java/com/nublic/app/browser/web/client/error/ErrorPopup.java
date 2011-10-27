package com.nublic.app.browser.web.client.error;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.PopupPanel;

public class ErrorPopup extends PopupPanel implements HasText {
	ErrorContent content;

	public ErrorPopup(String message) {
		content = new ErrorContent(message, this);
		this.add(content);
	}

	public static void showError(String message) {
		ErrorPopup error = new ErrorPopup(message);
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

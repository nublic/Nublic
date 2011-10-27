package com.nublic.app.browser.web.client.error;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class ErrorContent extends Composite implements HasText {
	private static ErrorContentUiBinder uiBinder = GWT.create(ErrorContentUiBinder.class);
	interface ErrorContentUiBinder extends UiBinder<Widget, ErrorContent> { }

//	@UiField PopupPanel rootPanel;
	@UiField Label messageLabel;
	@UiField Button closeButton;
	PopupPanel parent;
	
	public ErrorContent() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public ErrorContent(String message, PopupPanel parent) {
		initWidget(uiBinder.createAndBindUi(this));
		messageLabel.setText(message);
		this.parent = parent;
	}
	
	public void setText(String text) {
		messageLabel.setText(text);
	}
	
	public String getText() {
		return messageLabel.getText();
	}

	@UiHandler("closeButton")
	void onClick(ClickEvent e) {
//		rootPanel.hide();
		parent.hide();
	}	

}

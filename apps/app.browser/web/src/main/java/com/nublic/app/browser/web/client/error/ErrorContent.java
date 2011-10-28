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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ErrorContent extends Composite implements HasText {
	private static ErrorContentUiBinder uiBinder = GWT.create(ErrorContentUiBinder.class);
	interface ErrorContentUiBinder extends UiBinder<Widget, ErrorContent> { }

//	@UiField PopupPanel rootPanel;
	@UiField Label messageLabel;
	@UiField Button closeButton;
	@UiField VerticalPanel errorRoot;
	PopupPanel parent;
	Widget internalWidget; // We'll let define a widget as the error message
	
	public ErrorContent(String message, PopupPanel parent) {
		initWidget(uiBinder.createAndBindUi(this));
		messageLabel.setText(message);
		this.parent = parent;
		internalWidget = null;
	}
	
	public ErrorContent(PopupPanel parent) {
		initWidget(uiBinder.createAndBindUi(this));
		internalWidget = null;
		this.parent = parent;
	}

	public void setText(String text) {
		if (internalWidget != null) {
			errorRoot.remove(internalWidget);
			internalWidget = null;
		}
		messageLabel.setText(text);
	}
	
	public String getText() {
		return messageLabel.getText();
	}
	
	public void setInternalWidget(Widget w) {
		messageLabel.setText("");
		internalWidget = w;
		errorRoot.insert(internalWidget, 0);
	}
	
	public Widget getWidget() {
		return internalWidget;
	}

	@UiHandler("closeButton")
	void onClick(ClickEvent e) {
//		rootPanel.hide();
		parent.hide();
	}

}

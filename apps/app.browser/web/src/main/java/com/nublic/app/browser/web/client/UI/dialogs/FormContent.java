package com.nublic.app.browser.web.client.UI.dialogs;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;

public class FormContent extends Composite implements HasText {
	private static FormContentUiBinder uiBinder = GWT.create(FormContentUiBinder.class);
	interface FormContentUiBinder extends UiBinder<Widget, FormContent> { }

	@UiField Label titleLabel;
	@UiField Label closeLabel;
	@UiField Button cancelButton;
	@UiField Button acceptButton;
	@UiField TextBox textBox;
	ArrayList<ClickHandler> closeHandlers;
	
	
	// DONE: fix layout problems in chrome
	public FormContent() {
		this ("Enter text", "");
	}
	
	public FormContent(String title) {
		this(title, "");
	}
	
	public FormContent(String title, String defaultText) {
		initWidget(uiBinder.createAndBindUi(this));
		
		closeHandlers = new ArrayList<ClickHandler>();
		titleLabel.setText(title);
		textBox.setTitle(title);
		textBox.setText(defaultText);
	}

	
	public void addCloseHandler(ClickHandler handler) {
		closeHandlers.add(handler);
	}
	
	@UiHandler("closeLabel")
	void onCloseLabelClick(ClickEvent event) {
		close(event);
	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		onCancel();
		close(event);
	}

	@UiHandler("acceptButton")
	void onAcceptButtonClick(ClickEvent event) {
		onAccept();
		close(event);
	}
	
	private void close(ClickEvent event) {
		for (ClickHandler handler : closeHandlers) {
			handler.onClick(event);
		}
	}
	
	public void onAccept() { }
	
	public void onCancel() { }
	
	@Override
	public String getText() {
		return textBox.getText();
	}

	@Override
	public void setText(String text) {
		textBox.setText(text);
	}

}


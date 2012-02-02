package com.nublic.app.music.client.ui.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.SimplePanel;

public class GeneralPopup extends Composite {
	private static GeneralPopupUiBinder uiBinder = GWT.create(GeneralPopupUiBinder.class);
	interface GeneralPopupUiBinder extends UiBinder<Widget, GeneralPopup> { }

	@UiField Label titleLabel;
	@UiField Label closeLabel;
	@UiField Button cancelButton;
	@UiField Button deleteButton;
	@UiField Button acceptButton;
	@UiField SimplePanel mainPanel;
	
	CancelHandler cancelHandler;
	DeleteHandler deleteHandler;
	AcceptHandler acceptHandler;

	public GeneralPopup(String title, CancelHandler cancel, DeleteHandler delete, AcceptHandler accept) {
		initWidget(uiBinder.createAndBindUi(this));
		
		if (cancel == null) {
			cancelButton.setVisible(false);
		} else {
			cancelHandler = cancel;
		}
		
		if (delete == null) {
			deleteButton.setVisible(false);
		} else {
			deleteHandler = delete;
		}
		
		if (accept == null) {
			acceptButton.setVisible(false);
		} else {
			acceptHandler = accept;
		}
		titleLabel.setText(title);
	}
	
	public void setWidget(Widget w) {
		mainPanel.setWidget(w);
	}
	
	// Handlers
	@UiHandler("closeLabel")
	void onCloseLabelClick(ClickEvent event) {
		cancel();
	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		cancel();
	}

	@UiHandler("acceptButton")
	void onAcceptButtonClick(ClickEvent event) {
		accept();
	}
	
	@UiHandler("deleteButton")
	void onDeleteButtonClick(ClickEvent event) {
		delete();
	}
	
	public void accept() {
		acceptHandler.onAccept();
	}
	
	public void cancel() {
		cancelHandler.onCancel();
	}
	
	public void delete() {
		deleteHandler.onDelete();
	}
}

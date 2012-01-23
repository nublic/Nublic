package com.nublic.app.browser.web.client.UI.dialogs;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.UI.actions.UploadAction;

public class UploadContent extends Composite {
	private static UploadContentUiBinder uiBinder = GWT.create(UploadContentUiBinder.class);
	interface UploadContentUiBinder extends UiBinder<Widget, UploadContent> { }

	@UiField Label titleLabel;
	@UiField Label closeLabel;
	@UiField Button cancelButton;
	@UiField Button acceptButton;
	@UiField FileUpload uploadBox;
	ArrayList<ClickHandler> closeHandlers;
	String pathTo = "";
	
	public UploadContent() {
		this ("Select the file to upload");
	}
	
	public UploadContent(String title) {
		initWidget(uiBinder.createAndBindUi(this));
		closeHandlers = new ArrayList<ClickHandler>();
		titleLabel.setText(title);
		uploadBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				titleLabel.setText("Upload " + uploadBox.getFilename());				
			}
		});
	}
	
	public void addCloseHandler(ClickHandler handler) {
		closeHandlers.add(handler);
	}
	
	public void setCreationPath(String createInPath) {
		pathTo = createInPath;
	}
	
	@UiHandler("closeLabel")
	void onCloseLabelClick(ClickEvent event) {
		close(event);
	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		close(event);
	}

	@UiHandler("acceptButton")
	void onAcceptButtonClick(ClickEvent event) {
		UploadAction.doUpload(pathTo, uploadBox);
		close(event);
	}
	
	private void close(ClickEvent event) {
		for (ClickHandler handler : closeHandlers) {
			handler.onClick(event);
		}
	}

}

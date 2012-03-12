package com.nublic.util.widgets;

import java.util.EnumSet;

import com.google.gwt.user.client.ui.FileUpload;

public class UploadPopup extends Popup {
	
	FileUpload box;

	public UploadPopup(String title, EnumSet<PopupButton> buttonsToShow, String customLabel) {
		super(title, buttonsToShow, null, customLabel);
		
		box = new FileUpload();
		box.setWidth("100%");
		box.setHeight("100%");
		this.addWidget(box);
		
		setInnerHeight(140);
		addButtonHandler(PopupButton.CLOSE, POPUP_CLOSE);
		addButtonHandler(PopupButton.CANCEL, POPUP_CLOSE);
	}
	
	public UploadPopup(String title, EnumSet<PopupButton> buttonsToShow) {
		this(title, buttonsToShow, null);
	}
	
	public UploadPopup(String title) {
		this(title, EnumSet.of(PopupButton.UPLOAD, PopupButton.CANCEL));
	}
	
	public FileUpload getFileUpload() {
		return box;
	}
}

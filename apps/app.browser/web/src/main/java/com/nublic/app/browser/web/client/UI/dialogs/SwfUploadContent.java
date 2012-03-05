package com.nublic.app.browser.web.client.UI.dialogs;

import org.swfupload.client.SWFUpload;
import org.swfupload.client.SWFUpload.ButtonAction;
import org.swfupload.client.event.FileDialogCompleteHandler;
import org.swfupload.client.UploadBuilder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class SwfUploadContent extends Composite {

	private static SwfUploadContentUiBinder uiBinder = GWT.create(SwfUploadContentUiBinder.class);
	@UiField HTMLPanel htmlPanel;
	@UiField TextBox nameBox;
	SWFUpload upload;

	static final String BUTTON_STYLE = "margin: 0; font-family: \"Helvetica Neue\", Helvetica, Arial, sans-serif;"
			+ "font-size: 13px;"
			+ "line-height: 18px;"
			+ "color: #0088CC;"
			+ "cursor: pointer;";

	interface SwfUploadContentUiBinder extends UiBinder<Widget, SwfUploadContent> {
	}
	
	public SWFUpload getUploadInfo() {
		return upload;
	}

	public SwfUploadContent() {
		initWidget(uiBinder.createAndBindUi(this));

		final UploadBuilder builder = new UploadBuilder();
		builder.setFileTypes("*.*");
		builder.setFileTypesDescription("Any file");

		// Configure the button to display
		builder.setButtonPlaceholderID("swfupload");
		// builder.setButtonImageURL("XPButtonUploadText_61x22.png");
		builder.setButtonWidth(55);
		builder.setButtonHeight(22);
		builder.setButtonText("<span class=\"label\">Browse</span>");
		builder.setButtonTextStyle(".label { " + BUTTON_STYLE + " }");
		builder.setButtonTextLeftPadding(7);
		builder.setButtonTextTopPadding(4);

		Element placeHolder = DOM.createDiv();
		placeHolder.setId("swfupload");
		htmlPanel.getElement().appendChild(placeHolder);

		// Use ButtonAction.SELECT_FILE to only allow selection of a single file
		builder.setButtonAction(ButtonAction.SELECT_FILES);
		
		builder.setFileDialogCompleteHandler(new FileDialogCompleteHandler() {
			@Override
			public void onFileDialogComplete(FileDialogCompleteEvent e) {
				if (e.getFilesSelected() > 0) {
					nameBox.setText(upload.getFile(0).getName());
				}
			}
		});

		this.addAttachHandler(new AttachEvent.Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached()) {
					upload = builder.build();
				}
			}
		});
	}

}

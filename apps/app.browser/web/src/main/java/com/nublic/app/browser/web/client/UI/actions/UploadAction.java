package com.nublic.app.browser.web.client.UI.actions;

import org.swfupload.client.UploadBuilder;
import org.swfupload.client.SWFUpload.ButtonAction;
import org.swfupload.client.event.UploadErrorHandler;
import org.swfupload.client.event.UploadProgressHandler;
import org.swfupload.client.event.UploadStartHandler;
import org.swfupload.client.event.UploadSuccessHandler;
import org.swfupload.client.event.UploadErrorHandler.UploadErrorEvent;
import org.swfupload.client.event.UploadProgressHandler.UploadProgressEvent;
import org.swfupload.client.event.UploadStartHandler.UploadStartEvent;
import org.swfupload.client.event.UploadSuccessHandler.UploadSuccessEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.nublic.app.browser.web.client.Resources;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.model.FolderNode;
import com.nublic.util.messages.PostRedirectHelper;
import com.nublic.util.widgets.BootstrapProgressBar;

public class UploadAction extends ActionWidget implements Handler {
	
	static final String FLASH_BUTTON_STYLE =
			"margin: 0; font-family: \"Helvetica Neue\", Helvetica, Arial, sans-serif;"
			+ "font-size: 13px;"
			+ "line-height: 18px;"
			+ "color: #0088CC;"
			+ "cursor: pointer;";
	
	BrowserUi ui;

	public UploadAction(BrowserUi stateProvider) {
		super(Resources.INSTANCE.addfile(), "Upload file", stateProvider);
		
		if (stateProvider.hasFlashPlayer()) {
			// Let's change to use flash
			this.ui = stateProvider;
			this.addAttachHandler(this);
		}
	}
	

	@Override
	public void onAttachOrDetach(AttachEvent event) {
		if (event.isAttached()) {
			final UploadBuilder builder = new UploadBuilder();
			builder.setFileTypes("*.*");
			builder.setFileTypesDescription("Any file");
			// Configure the button to display
			builder.setButtonPlaceholderID(actionLink.getElement().getId());
			// builder.setButtonImageURL("XPButtonUploadText_61x22.png");
			builder.setButtonWidth(55);
			builder.setButtonHeight(22);
			builder.setButtonText("<span class=\"label\">Browse</span>");
			builder.setButtonTextStyle(".label { " + FLASH_BUTTON_STYLE + " }");
			builder.setButtonTextLeftPadding(7);
			builder.setButtonTextTopPadding(4);
			// Use ButtonAction.SELECT_FILE to only allow selection of a single file
			builder.setButtonAction(ButtonAction.START_UPLOAD);
			
			final VerticalPanel p = new VerticalPanel();
			final Label feedbackLabel = new Label("Uploading...");
			feedbackLabel.setWidth("160px");
			feedbackLabel.getElement().setAttribute("style", 
					"width: 160px; height: 20px; white-space: nowrap; " +
					"overflow: hidden; text-overflow: ellipsis; " +
					"-o-text-overflow: ellipsis; -moz-binding: url('ellipsis.xml#ellipsis');");
			final BootstrapProgressBar progressBar =
					new BootstrapProgressBar("progress-info", "progress-striped", "active");
			progressBar.setWidth("160px");
			p.add(feedbackLabel);
			p.add(progressBar);
			
			builder.setUploadStartHandler(new UploadStartHandler() {
				@Override
				public void onUploadStart(UploadStartEvent e) {
					ui.addToTaskList(p);
					feedbackLabel.setText("Uploading " + e.getFile().getName() + "...");
				}
			});
			builder.setUploadProgressHandler(new UploadProgressHandler() {
				@Override
				public void onUploadProgress(UploadProgressEvent e) {
					int complete = Math.round(100.0F * e.getBytesComplete() / e.getBytesTotal());
					progressBar.setProgress(complete);
				}
			});
			builder.setUploadSuccessHandler(new UploadSuccessHandler() {
				
				@Override
				public void onUploadSuccess(UploadSuccessEvent e) {
					progressBar.setProgress(100);
					progressBar.removeInnerStyleName("active");
					progressBar.removeInnerStyleName("progress-info");
					progressBar.addInnerStyleName("progress-success");
					
					ui.removeFromTaskList(p);
				}
			});
			builder.setUploadErrorHandler(new UploadErrorHandler() {
				
				@Override
				public void onUploadError(UploadErrorEvent e) {
					progressBar.setProgress(100);
					progressBar.removeInnerStyleName("active");
					progressBar.removeInnerStyleName("progress-info");
					progressBar.addInnerStyleName("progress-danger");
					ui.removeFromTaskList(p);
				}
			});
			
			builder.build();
		}
	}
	
	public static void doUpload(String pathTo, FileUpload uploadWidget, final BrowserUi stateProvider) {		
		PostRedirectHelper sendFileHelper = new PostRedirectHelper(URL.encode(GWT.getHostPageBaseURL() + "server/upload"));
		sendFileHelper.addParam("path", pathTo);
		int backSlashIndex = uploadWidget.getFilename().lastIndexOf("\\");
		String fileName = uploadWidget.getFilename();
		if (backSlashIndex >= 0) {
			fileName = fileName.substring(backSlashIndex + 1); 
		}
		sendFileHelper.addParam("name", fileName);
		sendFileHelper.addParam("Filedata", uploadWidget);
		
		final Label feedbackLabel = new Label("Uploading " + fileName + "...");
		stateProvider.addToTaskList(feedbackLabel);
		
		sendFileHelper.send(new SubmitCompleteHandler() {
			
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				stateProvider.removeFromTaskList(feedbackLabel);
			}
		});
	}

	@Override
	public void executeAction() {
		stateProvider.showUploadPopup();
	}

	@Override
	public Availability getAvailability() {
		if (stateProvider.getSelectedFiles().isEmpty()) {
			FolderNode n = stateProvider.getShowingFolder();
			if (n != null && n.isWritable()) {
				return Availability.AVAILABLE;
			} else {
				return Availability.UNCLICKABLE;
			}
		} else {
			return Availability.HIDDEN;
		}
	}

}

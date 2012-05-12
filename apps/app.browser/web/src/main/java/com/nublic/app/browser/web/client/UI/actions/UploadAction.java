package com.nublic.app.browser.web.client.UI.actions;

import java.util.ArrayList;

import org.swfupload.client.SWFUpload;
import org.swfupload.client.ButtonAction;
import org.swfupload.client.ButtonCursor;
import org.swfupload.client.WindowMode;
import org.swfupload.client.UploadBuilder;
import org.swfupload.client.event.FileDialogCompleteHandler;
import org.swfupload.client.event.SWFUploadLoadedHandler;
import org.swfupload.client.event.UploadErrorHandler;
import org.swfupload.client.event.UploadProgressHandler;
import org.swfupload.client.event.UploadStartHandler;
import org.swfupload.client.event.UploadSuccessHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.Resources;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.model.FolderNode;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.PostRedirectHelper;
import com.nublic.util.messages.SequenceHelper;
import com.nublic.util.tuples.Pair;
import com.nublic.util.widgets.BootstrapProgressBar;

public class UploadAction extends ActionWidget implements Handler {
	
	static final String FLASH_BUTTON_STYLE =
			".label { margin: 0; font-family: \"Helvetica Neue\", Helvetica, Arial, sans-serif;"
			+ "font-size: 13px;"
			+ "line-height: 18px;"
			+ "color: #0088CC;"
			+ "cursor: pointer; }";
	static final String DISABLED_FLASH_BUTTON_STYLE =
			".label { margin: 0; font-family: \"Helvetica Neue\", Helvetica, Arial, sans-serif;"
			+ "font-size: 13px;"
			+ "line-height: 18px;"
			+ "color: #808080;"
			+ "cursor: pointer; }";
	
	BrowserUi ui;
	Element childElement = null;
	SWFUpload upload = null;
	boolean uploadReady = false;

	public UploadAction(BrowserUi stateProvider) {
		super(Resources.INSTANCE.addfile(), Constants.I18N.uploadFile(), stateProvider);
		
		upload = null;
		if (stateProvider.hasFlashPlayer()) {
			// Let's change to use flash
			this.ui = stateProvider;
			this.addAttachHandler(this);
		}
	}
	
	static int build_widget_z_index = 20;
	
	public static Pair<Element, SWFUpload> buildUploadWidget(final Element e, final BrowserUi ui,
			final int width, final int height, final String text, final String style, final String imageUrl,
			final SWFUploadLoadedHandler readyH) {
		return buildUploadWidget(e, ui, width, height, text, style, imageUrl, readyH, "0", "0");
	}
	
	public static Pair<Element, SWFUpload> buildUploadWidget(final Element e, final BrowserUi ui,
			final int width, final int height, final String text, final String style, final String imageUrl,
			final SWFUploadLoadedHandler readyH, String left, String top) {
		if (!e.getAttribute("style").contains("position: relative;")) {
		  e.setAttribute("style", e.getAttribute("style") + " position: relative;");
		}
		
		final Element child = DOM.createDiv();
		final String childName = "upload" + String.valueOf(Math.abs(Random.nextInt()));
		child.setAttribute("style", "position: absolute; left: " + left + "; top: " + top + "; z-index: 100; width: " + width + "px; height: " + height + "px;");
		child.setId(childName);
		final Element replacedChild = DOM.createDiv();
		final String replacedChildName = "upload" + String.valueOf(Math.abs(Random.nextInt()));
		replacedChild.setAttribute("width", "100%");
		replacedChild.setId(replacedChildName);
		child.appendChild(replacedChild);
		e.appendChild(child);
		
		final UploadBuilder builder = new UploadBuilder();
		builder.setFileTypes("*.*");
		builder.setFileTypesDescription("Any file");
		// Configure the button to display
		builder.setButtonPlaceholderID(replacedChildName);
		builder.setButtonWidth(width);
		builder.setButtonHeight(height);
                builder.preventSWFCaching(false);
		if (text != null) {
			builder.setButtonText(text);
		}
		if (style != null) {
			builder.setButtonTextStyle(style);
		}
		if (imageUrl != null) {
			builder.setButtonImageURL(imageUrl);
		}
		builder.setButtonTextLeftPadding(0);
		builder.setButtonTextTopPadding(0);
		// Use ButtonAction.SELECT_FILE to only allow selection of a single file
		builder.setButtonAction(ButtonAction.SELECT_FILE);
		builder.setButtonCursor(ButtonCursor.HAND);
		builder.setWindowMode(WindowMode.TRANSPARENT);
		
		if (readyH != null) {
			builder.setSWFUploadLoadedHandler(readyH);
		}
		
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
		
		// HACK: use a list to be able to retrieve the upload item
		final ArrayList<SWFUpload> hack = new ArrayList<SWFUpload>();
		
		builder.setFileDialogCompleteHandler(new FileDialogCompleteHandler() {
			@Override
			public void onFileDialogComplete(FileDialogCompleteEvent fdce) {
				// HACK: get the first item, which is the object to get
				final SWFUpload upload = hack.get(0);
				// Do real work
				if (upload.getFile(0) != null) {
					SequenceHelper.sendJustOne(new Message() {
						@Override
						public String getURL() {
							return GWT.getHostPageBaseURL() + "server/upload-in-phases/phase1";
						}

						@Override
						public void onSuccess(Response response) {
							if (response.getStatusCode() == Response.SC_OK) {
								// Make the upload
								upload.setUploadURL(URL.encode(GWT.getHostPageBaseURL() + 
										"server/upload-in-phases/phase2/" + response.getText()));
								upload.addPostParam("path", ui.getShowingPath());
								upload.addPostParam("name", upload.getFile(0).getName());
								upload.startUpload();
								// Hide the previous item
								// child.getFirstChildElement().setAttribute("width", "1px;");
								// child.getFirstChildElement().setAttribute("height", "1px;");
								// child.setAttribute("style", "height: 1px; width: 1px; z-index: 1000;");
								// Add a new item
								buildUploadWidget(e, ui, width, height, text, style, imageUrl, readyH);
							}
						}

						@Override
						public void onError() {
							ErrorPopup.showError(Constants.I18N.errorUploading());
						}
						
					}, RequestBuilder.GET);
				}
			}
		});
		builder.setUploadStartHandler(new UploadStartHandler() {
			@Override
			public void onUploadStart(UploadStartEvent e) {
				ui.addToTaskList(p);
				feedbackLabel.setText(Constants.I18N.uploadingFile(e.getFile().getName()));
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
				// HACK: get the first item, which is the object to get
				SWFUpload upload = hack.get(0);
				upload.destroy();
				child.removeFromParent();
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
				// HACK: get the first item, which is the object to get
				SWFUpload upload = hack.get(0);
				upload.destroy();
				child.removeFromParent();
			}
		});
		
		SWFUpload uWidget = builder.build();
		hack.add(uWidget);
		// Special fix for Google Chrome
		Element object = child.getFirstChildElement();
		object.setAttribute("classid", "clsid:d27cdb6e-ae6d-11cf-96b8-444553540000");
		// Return our beloved widget
		return new Pair<Element, SWFUpload>(child, uWidget);
	}
	

	@Override
	public void onAttachOrDetach(AttachEvent event) {
		if (event.isAttached()) {
			Pair<Element, SWFUpload> e = buildUploadWidget(this.getElement(), ui, 150, 36,
					"                                 ", FLASH_BUTTON_STYLE, null, new SWFUploadLoadedHandler() {
				@Override
				public void onSWFUploadLoaded() {
					changeTextButtonState(_getAvailability());
					uploadReady = true;
				}
			});
			upload = e._2;
			childElement = e._1;
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
		
		final Label feedbackLabel = new Label(Constants.I18N.uploadingFile(fileName));
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
		if (!stateProvider.hasFlashPlayer()) {
			stateProvider.showUploadPopup();
		}
	}
	
	@Override
	public Availability getAvailability() {
		Availability a = _getAvailability();
		if (uploadReady) {
			upload.setButtonDisabled(a != Availability.AVAILABLE);
		}
		if (childElement != null) {
			String zIndex = (a != Availability.AVAILABLE) ? "-100" : "100";
			childElement.setAttribute("style", "position: absolute; left: 0; top: 0; z-index: " + zIndex + "; width: 150px; height: 36px;");
		}
		return a;
	}
	
	private void changeTextButtonState(Availability a) {
		upload.setButtonDisabled(a != Availability.AVAILABLE);
	}
	
	public Availability _getAvailability() {
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

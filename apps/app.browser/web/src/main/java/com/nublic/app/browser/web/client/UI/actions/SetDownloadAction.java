package com.nublic.app.browser.web.client.UI.actions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.UI.FileWidget;

public class SetDownloadAction extends ActionWidget {

	public SetDownloadAction(BrowserUi stateProvider) {
		super("images/download.png", "Download selected files", stateProvider);
	}

	@Override
	public void executeAction() {
		StringBuilder setOfFiles = new StringBuilder();
		for (Widget w : stateProvider.getSelectedFiles()) {
			if (setOfFiles.length() != 0) {
				setOfFiles.append(":");
			}
			setOfFiles.append(((FileWidget) w).getPath());
		}
		String path = stateProvider.getPath();

		// send Post
		FormPanel form = new FormPanel();
		VerticalPanel formContent = new VerticalPanel();
		form.add(formContent);
		
		form.setAction(GWT.getHostPageBaseURL() + "server/zip-set");
		form.setEncoding(FormPanel.ENCODING_URLENCODED);
		form.setMethod(FormPanel.METHOD_POST);
		
		// Set params
		TextBox param1 = new TextBox();
		param1.setName("filename");
		if (path.contains("/")) {
			param1.setValue(path.substring(path.lastIndexOf("/") + 1) + "." + Constants.COMPRESSED_TYPE);
		} else {
			param1.setValue(path + "." + Constants.COMPRESSED_TYPE);
		}
		formContent.add(param1);
		
		TextBox param2 = new TextBox();
		param2.setName("files");
		param2.setValue(setOfFiles.toString());
		formContent.add(param2);

		form.setVisible(false);
		RootPanel.get().add(form);
		
		// Make the request
		form.submit();
//		Message m = new Message() {
//			@Override
//			public void onSuccess(Response response) {
////				Window.open(GWT.getHostPageBaseURL() + "server/zip/" + stateProvider.getPath(), "_self", "");
//			}
//			@Override
//			public void onError() {}
//			@Override
//			public String getURL() {
//				return URL.encode(GWT.getHostPageBaseURL() + "server/zip-set");
//			}
//		};
//		StringBuilder setOfFiles = new StringBuilder();
//		for (Widget w : stateProvider.getSelectedFiles()) {
//			if (setOfFiles.length() != 0) {
//				setOfFiles.append(":");
//			}
//			setOfFiles.append(((FileWidget) w).getPath());
//		}
//		String path = stateProvider.getPath();
//		m.addParam("files", setOfFiles.toString());
//		if (path.contains("/")) {
//			m.addParam("filename", path.substring(path.lastIndexOf("/") + 1));
//		} else {
//			m.addParam("filename", path);
//		}
//		SequenceHelper.sendJustOne(m, RequestBuilder.POST);
	}

	@Override
	public Availability getAvailability() {
		if (stateProvider.getSelectedFiles().size() < 2) {
			return Availability.HIDDEN;
		} else {
			return Availability.AVAILABLE;
		}
	}

}

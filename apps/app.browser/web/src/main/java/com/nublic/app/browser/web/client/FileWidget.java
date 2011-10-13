package com.nublic.app.browser.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class FileWidget extends Composite {

	private static FileWidgetUiBinder uiBinder = GWT
			.create(FileWidgetUiBinder.class);

	interface FileWidgetUiBinder extends UiBinder<Widget, FileWidget> {
	}

	public FileWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public FileWidget(FileNode n) {
		initWidget(uiBinder.createAndBindUi(this));
	}
}

package com.nublic.app.browser.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class FileWidget extends Composite {

	private static FileWidgetUiBinder uiBinder = GWT.create(FileWidgetUiBinder.class);
	
	FileNode node;
	String path;
	
	@UiField Label fileName;
	@UiField Image fileImage;

	interface FileWidgetUiBinder extends UiBinder<Widget, FileWidget> {
	}

	public FileWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public FileWidget(FileNode n, String path) {
		initWidget(uiBinder.createAndBindUi(this));
		this.node = n;
		this.path = path;
		//String pathEncoded = URL.encodePathSegment(path + n.getName());
		String url = URL.encode(GWT.getHostPageBaseURL() + "server/thumbnail/" + path + "/" + n.getName());
		fileImage.setUrl(url);
		fileName.setText(n.getName());
	}
}

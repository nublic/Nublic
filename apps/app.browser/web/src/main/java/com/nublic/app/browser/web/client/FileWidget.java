package com.nublic.app.browser.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class FileWidget extends Composite {

	private static FileWidgetUiBinder uiBinder = GWT.create(FileWidgetUiBinder.class);
	
	FileNode node;
	String path;

	@UiField Hyperlink fileThumbnail;
	@UiField Hyperlink fileName;
	@UiField Image altThumbnail;
	@UiField Label altName;

	interface FileWidgetUiBinder extends UiBinder<Widget, FileWidget> {
	}

	public FileWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	// path is the path of the folder where the file is placed
	public FileWidget(FileNode n, String path) {
		initWidget(uiBinder.createAndBindUi(this));
		
		// init internal variables
		this.node = n;
		this.path = path + "/" + n.getName();
		
		// Gets the thumbnail of the file
		String url = URL.encode(GWT.getHostPageBaseURL() + "server/thumbnail/" + this.path);
		
		String viewType = node.getView();
		if (node.getMime().equals(Constants.FOLDER_MIME)) {
			viewType = Constants.FOLDER_TYPE;
		}
		// Check whether the file has a view or not (to files with views and folders we'll show links)
		if (viewType != null) {
			// Set unused fields to not visible
			altThumbnail.setVisible(false);
			altName.setVisible(false);

			// Add the image thumbnail to the hypertext widget
			Image fileImage = new Image(url);
			fileThumbnail.getElement().getChild(0).appendChild(fileImage.getElement()); 
			
			// Set up name
			fileName.setText(n.getName());
			
			// Set the destination URL
			setURL(viewType); // modifies both fileThumbnail and fileName
		} else {
			// Set unused fields to not visible
			fileThumbnail.setVisible(false);
			fileName.setVisible(false);
			
			// Gets the thumbnail of the file
			altThumbnail.setUrl(url); 
			
			// Set up name
			altName.setText(n.getName());
		}
	}

	private void setURL(String viewType) {
		String target = null;
		if (viewType.equals(Constants.IMAGE_TYPE)) {
			target = Constants.IMAGE_VIEW + "?" + Constants.PATH_PARAMETER + "=" + path;
		} else if (viewType.equals(Constants.DOCUMENT_TYPE)) {
		} else if (viewType.equals(Constants.MUSIC_TYPE)) {
		} else if (viewType.equals(Constants.VIDEO_TYPE)) {
		} else if (viewType.equals(Constants.FOLDER_TYPE)) {
			target = Constants.BROWSER_VIEW + "?" + Constants.PATH_PARAMETER + "=" + path;
		}
		fileThumbnail.setTargetHistoryToken(target);
		fileName.setTargetHistoryToken(target);
	}
}

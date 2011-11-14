package com.nublic.app.browser.web.client.UI;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.model.FileNode;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.PushButton;

public class FileWidget extends Composite {

	private static FileWidgetUiBinder uiBinder = GWT.create(FileWidgetUiBinder.class);
	
	interface FileWidgetUiBinder extends UiBinder<Widget, FileWidget> {
	}
	
	// CSS Styles defined in the .xml file
	interface FileStyle extends CssResource {
		String inLine();
	    String maxheight();
	    String ellipcenter();
	}
	
	FileNode node;
	String path;
	boolean mouseOver = false;
	Hyperlink fileThumbnail;
	Image altThumbnail;
	Hyperlink fileName;
	Label altName;
	@UiField VerticalPanel imagePanel;
	@UiField VerticalPanel textPanel;
	@UiField FileStyle style;
	@UiField CheckBox selectedBox;
	@UiField PushButton downloadButton;

	// path is the path of the folder where the file is placed
	public FileWidget(FileNode n, String path) {
		initWidget(uiBinder.createAndBindUi(this));
		
		// init internal variables
		this.node = n;
		if (path.equals("")) {
			this.path = n.getName();
		} else {
			this.path = path + "/" + n.getName();
		}
		
		// Gets the thumbnail of the file
		String url = URL.encode(GWT.getHostPageBaseURL() + "server/thumbnail/" + this.path);
		
		String viewType = node.getView();
		if (node.getMime().equals(Constants.FOLDER_MIME)) {
			viewType = Constants.FOLDER_TYPE;
		}
		// Check whether the file has a view or not (to files with views and to folders we'll show links)
		if (viewType != null) {
			// Create the widgets
			fileThumbnail = new Hyperlink();
			fileName = new Hyperlink();
			
			// Associate CSS styles
			fileThumbnail.getElement().addClassName(style.maxheight());
			fileName.getElement().addClassName(style.ellipcenter());
			
			// Add the image thumbnail to the hypertext widget
			Image fileImage = new Image(url);
			fileThumbnail.getElement().getChild(0).appendChild(fileImage.getElement()); 
			
			// Set up name
			fileName.setText(n.getName());
			fileName.setTitle(n.getName());
			
			// Set the destination URL
			setURL(viewType); // modifies both fileThumbnail and fileName
			
			// Add the widgets to the panels
			imagePanel.add(fileThumbnail);
			textPanel.add(fileName);
		} else {
			// Create the alternative widgets (which are not links)
			altThumbnail = new Image(url);
			altName = new Label(n.getName());
			altName.setTitle(n.getName());
			
			// Associate CSS styles
			altThumbnail.getElement().addClassName(style.maxheight());
			altName.getElement().addClassName(style.ellipcenter());
			
			// Add the widgets to the panels
			imagePanel.add(altThumbnail);
			textPanel.add(altName);
		}
		
		addMouseOverHandler(new MyMouseEventHandler());
		addMouseOutHandler(new MyMouseEventHandler());
		selectedBox.setVisible(false);
		downloadButton.setVisible(false);
	}

	private void setURL(String viewType) {
		String target = null;
		if (viewType.equals(Constants.IMAGE_TYPE)) {
			target = Constants.IMAGE_VIEW + "?" + Constants.PATH_PARAMETER + "=" + path;
		} else if (viewType.equals(Constants.DOCUMENT_TYPE)) {
			target = Constants.DOCUMENT_VIEW + "?" + Constants.PATH_PARAMETER + "=" + path;
		} else if (viewType.equals(Constants.MUSIC_TYPE)) {
			target = Constants.MUSIC_VIEW + "?" + Constants.PATH_PARAMETER + "=" + path;
		} else if (viewType.equals(Constants.VIDEO_TYPE)) {
			target = Constants.VIDEO_VIEW + "?" + Constants.PATH_PARAMETER + "=" + path;
		} else if (viewType.equals(Constants.FOLDER_TYPE)) {
			target = Constants.BROWSER_VIEW + "?" + Constants.PATH_PARAMETER + "=" + path;
		} else if (viewType.equals(Constants.TEXT_TYPE)) {
			target = Constants.TEXT_VIEW + "?" + Constants.PATH_PARAMETER + "=" + path;
		} 
		if (fileThumbnail != null && fileName != null) {
			fileThumbnail.setTargetHistoryToken(target);
			fileName.setTargetHistoryToken(target);
		}
	}
	
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	public class MyMouseEventHandler implements MouseOverHandler, MouseOutHandler {
		public void onMouseOver(final MouseOverEvent moe) {
			selectedBox.setVisible(true);
			downloadButton.setVisible(true);
			mouseOver = true;
//			widget.addStyleName("my-mouse-over");
		}

		public void onMouseOut(final MouseOutEvent moe) {
			downloadButton.setVisible(false);
			if (!selectedBox.getValue()) {
				selectedBox.setVisible(false);
			}
			mouseOver = false;
//			widget.removeStyleName("my-mouse-over");
		}
	}
	
	public boolean isChecked() {
		return selectedBox.getValue();
	}
	
	public void setChecked(boolean checked) {
		if (!mouseOver) {
			selectedBox.setVisible(checked);
		}
		selectedBox.setValue(checked);
	}
}

package com.nublic.app.browser.web.client.UI;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.UI.actions.SingleDownloadAction;
import com.nublic.app.browser.web.client.model.FileNode;

public class FileWidget extends Composite implements HasMouseDownHandlers {

	private static FileWidgetUiBinder uiBinder = GWT.create(FileWidgetUiBinder.class);
	
	interface FileWidgetUiBinder extends UiBinder<Widget, FileWidget> {
	}
	
	// CSS Styles defined in the .xml file
	interface FileStyle extends CssResource {
		String inLine();
	    String maxheight();
	    String ellipcenter();
	    String shadowed();
	}

	FileNode node;
	String path;
	String url = null;
	boolean mouseOver = false;
	boolean hasPreview = false;
	Image fileImage;
	Hyperlink fileThumbnail;
	Image altThumbnail;
	Hyperlink fileName;
	Label altName;
	@UiField VerticalPanel imagePanel;
	@UiField VerticalPanel textPanel;
	@UiField FileStyle style;
	@UiField CheckBox selectedBox;
	@UiField PushButton downloadButton;

	List<CheckedChangeHandler> chekedChangeHandlers = new ArrayList<CheckedChangeHandler>();
	
	public void addCheckedChangeHandler(CheckedChangeHandler handler) {	 	
		chekedChangeHandlers.add(handler);
	}
	
	public List<CheckedChangeHandler> getCheckedChangeHandlers() {
		return chekedChangeHandlers;
	}
	
	// path is the path of the folder where the file is placed
//	public FileWidget(FileNode n, String path, DevicesManager pathConverter) {
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
//		realPath = pathConverter.getRealPath(this.path);
//		String url = URL.encode(GWT.getHostPageBaseURL() + "server/thumbnail/" + realPath);
		String url = URL.encode(GWT.getHostPageBaseURL() + "server/thumbnail/" + this.path);
		
		String viewType = node.getView();
		if (node.getMime().equals(Constants.FOLDER_MIME)) {
			viewType = Constants.FOLDER_TYPE;
		}
		// Check whether the file has a view or not (to files with views and to folders we'll show links)
		if (viewType != null) {
			hasPreview = true;
		}

		// To fileWidgets with previews we'll create hyperlinks
		if (hasPreview) {
			// Create the widgets
			fileThumbnail = new Hyperlink();
			fileName = new Hyperlink();
			
			// Associate CSS styles
			fileThumbnail.getElement().addClassName(style.maxheight());
			fileName.getElement().addClassName(style.ellipcenter());
			
			// Add the image thumbnail to the hypertext widget
			fileImage = new Image(url);
			fileThumbnail.getElement().getChild(0).appendChild(fileImage.getElement()); 
			
			// Set up name
			fileName.setText(n.getName());
			fileName.setTitle(n.getName());
			
			// Set the destination URL
			setURL(viewType); // modifies both fileThumbnail and fileName
			
			// Add the widgets to the panels
			imagePanel.add(fileThumbnail);
			textPanel.add(fileName);
			
			// Add handlers
			fileThumbnail.addDomHandler(new MyMouseEventHandler(), MouseDownEvent.getType());
			fileName.addDomHandler(new MyMouseEventHandler(), MouseDownEvent.getType());
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
			
			// Add handlers
			altThumbnail.addMouseDownHandler(new MyMouseEventHandler());
			altName.addMouseDownHandler(new MyMouseEventHandler());
		}
		addMouseOverHandler(new MyMouseEventHandler());
		addMouseOutHandler(new MyMouseEventHandler());
		// This doesn't work.. no reason
		// (making widgets draggables makes their children to not receive onMouseUp events, nor onClick)
//		addMouseUpHandler(new MouseUpHandler() {
//			@Override
//			public void onMouseUp(MouseUpEvent event) {
//				System.out.println("mouse up");
//				mouseOverActions();
//			}
//		});
		selectedBox.setVisible(false);
		downloadButton.setVisible(false);
		
		selectedBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				for (CheckedChangeHandler handler : chekedChangeHandlers) {
					handler.onChekedChange(FileWidget.this);
				}
			}
		});
	}

	public String getViewType() {
		return node.getView();
	}
	
	public String getMime() {
		return node.getMime();
	}
	
	public boolean isWritable() {
		return node.isWritable();
	}
	
	public String getPath() {
		return path;
	}
	
	public String getName() {
		return node.getName(); 
	}
	
	public double getLastUpdate() {
		return node.getLastUpdate();
	}
	
	public double getSize() {
		return node.getSize();
	}
	
	public Image getImage() {
		if (hasPreview) {
			return fileImage;
		} else {
			return altThumbnail;
		}
	}

	private void setURL(String viewType) {
//		String target = Constants.getView(viewType) + "?" + Constants.PATH_PARAMETER + "=" + realPath;
		url = Constants.getView(viewType) + "?" + Constants.PATH_PARAMETER + "=" + path;
		if (fileThumbnail != null && fileName != null) {
			fileThumbnail.setTargetHistoryToken(url);
			fileName.setTargetHistoryToken(url);
		}
	}
	
	public String getURL() {
		return url;
	}
	
	@UiHandler("downloadButton")
	void onDownloadButtonClick(ClickEvent event) {
		// TODO: Regression. this is not called anymore, using onmousedown (reported to library developer)
//		SingleDownloadAction.download(realPath);
		SingleDownloadAction.download(path);
	}
	
	@UiHandler("downloadButton")
	void onDownloadButtonMouseDown(MouseDownEvent event) {
//		SingleDownloadAction.download(realPath);
		SingleDownloadAction.download(path);
	}
	
	public void setCut() {
		if (hasPreview) {
			fileThumbnail.addStyleName(style.shadowed());
		} else {
			altThumbnail.addStyleName(style.shadowed());
		}
	}
	
	public void setUncut() {
		if (hasPreview) {
			fileThumbnail.removeStyleName(style.shadowed());
		} else {
			altThumbnail.addStyleName(style.shadowed());
		}
	}

	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
//		return addDomHandler(handler, LoseCaptureEvent.getType());
	}

	public class MyMouseEventHandler implements MouseOverHandler, MouseOutHandler, MouseDownHandler {
		public void onMouseOver(final MouseOverEvent moe) {
//			System.out.println("mouse over");
			mouseOverActions();
		}

		public void onMouseOut(final MouseOutEvent moe) {
//			System.out.println("mouse out");
			mouseOutActions();
		}
		
		@Override
		public void onMouseDown(MouseDownEvent event) {
//			System.out.println("mouse down");
			mouseOutActions();
		}
	}
	
	public void mouseOverActions() {
		selectedBox.setVisible(true);
		downloadButton.setVisible(true);
		mouseOver = true;
//		widget.addStyleName("my-mouse-over");
	}
	
	public void mouseOutActions() {
		downloadButton.setVisible(false);
		if (!selectedBox.getValue()) {
			// To maintain visible the boxes 
			selectedBox.setVisible(false);
		}
		mouseOver = false;
//		widget.removeStyleName("my-mouse-over");
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
	
	// To proper handling of FileWidgets lists
	@Override
	public boolean equals(Object o) {
		if (o instanceof FileWidget) {
			return ((FileWidget)o).getPath().equals(path);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return getPath().hashCode();
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
//		if (hasPreview) {
//			return fileThumbnail.addDomHandler(handler, MouseDownEvent.getType());
//		} else {
//			return altThumbnail.addMouseDownHandler(handler);
////			return altThumbnail.addDomHandler(handler, MouseDownEvent.getType());
//		}
		return addDomHandler(handler, MouseDownEvent.getType());
	}
	
//	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
//		return addDomHandler(handler, MouseUpEvent.getType());
//	}

}

package com.nublic.app.browser.web.client.UI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
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
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
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
import com.nublic.util.gwt.LocationUtil;

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
	    String childForHoverNotSelected();
	    String childForHoverSelected();
	}

	FileNode node;
	String path;
	String inPath;
	String url = null;
	String imageUrl = null;
	boolean hasPreview = false;
	Element divImage;
	// Image fileImage;
	Hyperlink fileThumbnail;
	// Image altThumbnail;
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
	
	public static native JsArray<Node> _getAttributes(Element elem) /*-{
	   return elem.attributes;
	}-*/;
	
	public Map<String, String> getAttributtes(Element element) {
		HashMap<String, String> m = new HashMap<String, String>();
		final JsArray<Node> attributes = _getAttributes(element);
		for (int i = 0; i < attributes.length(); i ++) {
		    final Node node = attributes.get(i);
		    String attributeName = node.getNodeName();
		    String attributeValue = node.getNodeValue();
		    m.put(attributeName, attributeValue);
		}
		return m;
	}
	
	// path is the path of the folder where the file is placed
	public FileWidget(FileNode n, String path) {
		initWidget(uiBinder.createAndBindUi(this));
		
		// init internal variables
		this.node = n;
		if (path.equals("")) {
			inPath = "";
			this.path = n.getName();
		} else {
			inPath = path;
			this.path = path + "/" + n.getName();
		}
		
		// Gets the thumbnail of the file
		imageUrl = LocationUtil.encodeURL(GWT.getHostPageBaseURL() + "server/thumbnail/" + this.path);
		if (!n.hasThumbnail()) {
			imageUrl = LocationUtil.encodeURL(GWT.getHostPageBaseURL() + "server/generic-thumbnail/" + n.getMime());
		}
		
		String viewType = node.getView();
		if (node.getMime().equals(Constants.FOLDER_MIME)) {
			viewType = Constants.FOLDER_TYPE;
		}
		// Check whether the file has a view or not (to files with views and to folders we'll show links)
		if (viewType != null) {
			hasPreview = true;
		}
		
		// Set the thumbnail
		divImage = DOM.createDiv();
		imageUrl = node.getImportantThumbnail() == null ? imageUrl : new Image(node.getImportantThumbnail()).getUrl();
		divImage.setAttribute("style", "height: 96px; width: 96px; background-image: url('" + imageUrl + "');" +
				" background-position: center center; background-repeat: no-repeat;");
		// fileImage = node.getImportantThumbnail() == null ? new Image(url) : new Image(node.getImportantThumbnail());

		// To fileWidgets with previews we'll create hyperlinks
		if (hasPreview) {
			// Create the widgets
			fileThumbnail = new Hyperlink();
			fileName = new Hyperlink();
			
			// Associate CSS styles
			fileThumbnail.getElement().addClassName(style.maxheight());
			fileName.getElement().addClassName(style.ellipcenter());
			
			// Add the image thumbnail to the hypertext widget
//			fileImage = new Image(url);
			// fileThumbnail.getElement().getChild(0).appendChild(fileImage.getElement()); 
			fileThumbnail.getElement().getChild(0).appendChild(divImage);
			
			// Set up name
			fileName.setText(n.getName());
			fileName.setTitle(n.getName());
			
			// Set the destination URL
			setURL(viewType); // modifies both fileThumbnail and fileName
			
			// Add the widgets to the panels
			imagePanel.add(fileThumbnail);
			textPanel.add(fileName);
			
			// Add handlers
			// fileThumbnail.addDomHandler(new MyMouseEventHandler(), MouseDownEvent.getType());
			// fileName.addDomHandler(new MyMouseEventHandler(), MouseDownEvent.getType());
		} else {
			// Create the alternative widgets (which are not links)
//			altThumbnail = new Image(url);
			// altThumbnail = fileImage;			
			altName = new Label(n.getName());
			altName.setTitle(n.getName());
			
			// Associate CSS styles
			// altThumbnail.getElement().addClassName(style.maxheight());
			divImage.addClassName(style.maxheight());
			altName.getElement().addClassName(style.ellipcenter());
			
			// Add the widgets to the panels
			// imagePanel.add(altThumbnail);
			imagePanel.getElement().appendChild(divImage);
			textPanel.add(altName);
			
			// Add handlers
			// altThumbnail.addMouseDownHandler(new MyMouseEventHandler());
			// altName.addMouseDownHandler(new MyMouseEventHandler());
		}
		// This doesn't work.. no reason
		// (making widgets draggables makes their children to not receive onMouseUp events, nor onClick)
//		addMouseUpHandler(new MouseUpHandler() {
//			@Override
//			public void onMouseUp(MouseUpEvent event) {
//				System.out.println("mouse up");
//				mouseOverActions();
//			}
//		});
		// selectedBox.setVisible(false);
		selectedBox.setValue(false, false);
		// downloadButton.setVisible(false);
		
		selectedBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				// Set CSS style
				if (event.getValue()) {
					selectedBox.removeStyleName(style.childForHoverNotSelected());
					selectedBox.addStyleName(style.childForHoverSelected());
				} else {
					selectedBox.removeStyleName(style.childForHoverSelected());
					selectedBox.addStyleName(style.childForHoverNotSelected());
				}
				// Notify other elements
				for (CheckedChangeHandler handler : chekedChangeHandlers) {
					handler.onChekedChange(FileWidget.this);
				}
			}
		});
	}

	public FileNode getNode() {
		return node;
	}
	
	public String getViewType() {
		return node.getView();
	}
	
	public String getMime() {
		return node.getMime();
	}
	
	public boolean isFolder() {
		return node.getMime().equals(Constants.FOLDER_MIME);
	}
	
	public boolean isWritable() {
		return node.isWritable();
	}
	
	public String getPath() {
		return path;
	}
	
	public String getInPath() {
		return inPath;
	}

	public void setInPath(String inPath) {
		this.inPath = inPath;
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
	
	/*public Image getImage() {
//		if (hasPreview) {
			return fileImage;
//		} else {
//			return altThumbnail;
//		}
	}*/
	
	public String getImageUrl() {
		return imageUrl;
	}

	private void setURL(String viewType) {
		String link = node.getImportantLink() == null ? path : node.getImportantLink();
		url = Constants.getView(viewType) + "?" + Constants.PATH_PARAMETER + "=" + link;

		if (fileThumbnail != null && fileName != null) {
			fileThumbnail.setTargetHistoryToken(url);
			fileName.setTargetHistoryToken(url);
		}
	}
	
	public String getURL() {
		return url;
	}
	
	// Regression. this is not called anymore, using onmousedown (reported to library developer)
	/*@UiHandler("downloadButton")
	void onDownloadButtonClick(ClickEvent event) {
		SingleDownloadAction.download(path, isFolder());
	}*/
	
	@UiHandler("downloadButton")
	void onDownloadButtonMouseDown(MouseDownEvent event) {
		SingleDownloadAction.download(path, isFolder());
	}
	
	public void setCut() {
		if (hasPreview) {
			fileThumbnail.addStyleName(style.shadowed());
		} else {
			divImage.addClassName(style.shadowed());
		}
	}
	
	public void setUncut() {
		if (hasPreview) {
			fileThumbnail.removeStyleName(style.shadowed());
		} else {
			divImage.addClassName(style.shadowed());
		}
	}

	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
//		return addDomHandler(handler, LoseCaptureEvent.getType());
	}
	
	public boolean isChecked() {
		return selectedBox.getValue();
	}
	
	public void setChecked(boolean checked) {
		/*if (!mouseOver) {
			selectedBox.setVisible(checked);
		}*/
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

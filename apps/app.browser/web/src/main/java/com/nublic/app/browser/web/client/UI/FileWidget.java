package com.nublic.app.browser.web.client.UI;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.UI.actions.SingleDownloadAction;
import com.nublic.app.browser.web.client.model.FileNode;
import com.nublic.util.gwt.LocationUtil;
import com.nublic.util.widgets.AnchorPanel;

public class FileWidget extends Composite implements HasMouseDownHandlers {

	private static FileWidgetUiBinder uiBinder = GWT.create(FileWidgetUiBinder.class);
	
	interface FileWidgetUiBinder extends UiBinder<Widget, FileWidget> {
	}
	
	// CSS Styles defined in the .xml file
	interface FileStyle extends CssResource {
		String inLine();
	    String maxmeasures();
	    String ellipcenter();
	    String shadowed();
	    String childForHoverNotSelected();
	    String childForHoverSelected();
	    String imageDecoration();
	    String noLinkStyle();
	}

	FileNode node;
	String path;
	String inPath;
	String url = null;
	String imageUrl = null;
	boolean hasPreview = false;
	@UiField AnchorPanel anchorPanel;
	@UiField Image fileThumbnail;
	@UiField Hyperlink fileName;
	@UiField FileStyle style;
	@UiField CheckBox selectedBox;
	@UiField PushButton downloadButton;
	@UiField VerticalPanel namePanel;

	List<CheckedChangeHandler> chekedChangeHandlers = new ArrayList<CheckedChangeHandler>();
	
	public void addCheckedChangeHandler(CheckedChangeHandler handler) {	 	
		chekedChangeHandlers.add(handler);
	}
	
	public List<CheckedChangeHandler> getCheckedChangeHandlers() {
		return chekedChangeHandlers;
	}
	
//	public static native JsArray<Node> _getAttributes(Element elem) /*-{
//	   return elem.attributes;
//	}-*/;
//	
//	public Map<String, String> getAttributtes(Element element) {
//		HashMap<String, String> m = new HashMap<String, String>();
//		final JsArray<Node> attributes = _getAttributes(element);
//		for (int i = 0; i < attributes.length(); i ++) {
//		    final Node node = attributes.get(i);
//		    String attributeName = node.getNodeName();
//		    String attributeValue = node.getNodeValue();
//		    m.put(attributeName, attributeValue);
//		}
//		return m;
//	}
	
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
		if (Constants.isFolderMime(node.getMime())) {
			viewType = Constants.FOLDER_TYPE;
		}
		// Check whether the file has a view or not (to files with views and to folders we'll show links)
		if (viewType != null) {
			hasPreview = true;
		}
		
		// Set the thumbnail
		if (node.getImportantThumbnail() == null) {
			fileThumbnail.setUrl(imageUrl);
		} else {
			fileThumbnail.setResource(node.getImportantThumbnail());
		}
		// Set up name
		fileName.setText(n.getName());
		fileName.setTitle(n.getName());
		fileThumbnail.setTitle(n.getName());
		

		// To fileWidgets with previews we'll create hyperlinks
		if (hasPreview) {			
			// Set the destination URL
			setURL(viewType); // modifies both fileThumbnail and fileName
		} else {
			// set useless URL
			deactivateLinks();
		}

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
		return Constants.isFolderMime(node.getMime());
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

	public String getImageUrl() {
		return imageUrl;
	}

	private void setURL(String viewType) {
		String link = node.getImportantLink() == null ? path : node.getImportantLink();
		url = Constants.getView(viewType) + "?" + Constants.PATH_PARAMETER + "=" + link;

		fileName.setTargetHistoryToken(url);
		anchorPanel.setHref("#" + url);
	}
	

	private void deactivateLinks() {
		fileName.setTargetHistoryToken(History.getToken());
		anchorPanel.setHref("#" + History.getToken());
		
		namePanel.addStyleName(style.noLinkStyle());
		fileThumbnail.addStyleName(style.noLinkStyle());
	}
	
	public String getURL() {
		return url;
	}
	
	// Regression. this is not called anymore, using onmousedown (reported to library developer)
//	@UiHandler("downloadButton")
//	void onDownloadButtonClick(ClickEvent event) {
//		SingleDownloadAction.download(path, isFolder());
//	}
	
	@UiHandler("downloadButton")
	void onDownloadButtonMouseDown(MouseDownEvent event) {
		SingleDownloadAction.download(path, isFolder());
	}
	
	public void setCut() {
		fileThumbnail.addStyleName(style.shadowed());
	}
	
	public void setUncut() {
		fileThumbnail.removeStyleName(style.shadowed());
	}

//	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
//		return addDomHandler(handler, MouseOverEvent.getType());
//	}
//
//	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
//		return addDomHandler(handler, MouseOutEvent.getType());
//	}
	
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
		return addDomHandler(handler, MouseDownEvent.getType());
	}

}

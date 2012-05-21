package com.nublic.app.browser.web.client.UI;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.Resources;
import com.nublic.app.browser.web.client.model.FileNode;

public class SelectionDetails extends Composite {
	private static SelectionDetailsUiBinder uiBinder = GWT.create(SelectionDetailsUiBinder.class);
	interface SelectionDetailsUiBinder extends UiBinder<Widget, SelectionDetails> {	}

	@UiField InfoStyle style;
	@UiField Label selectionNameLabel;
	@UiField VerticalPanel thumbnailPanel;
	@UiField Label info1Label;
	@UiField Label info2Label;
	@UiField Label dateLabel;
	
	public SelectionDetails() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	// CSS Styles defined in the .xml file
	interface InfoStyle extends CssResource {
	    String ellipsize();
	    String center();
	}
	
	public void changeInfo(Set<Widget> newSelection) {
		SelectionInfo info = getSelectionInfo(newSelection);
		selectionNameLabel.setText(info.title);
		selectionNameLabel.setTitle(info.title);
		if (info.onClickURL == null) {
			setImage(info.imageURL);
		} else {
			setHyperLink(info.imageURL, info.onClickURL);
		}
		info1Label.setText(info.firstLine);
		info2Label.setText(info.secondLine);
		dateLabel.setText(info.date);
	}
	
	public static SelectionInfo getSelectionInfo(Set<Widget> selectedFiles) {
		String title = null;
		String firstLine = null;
		String secondLine = null;
		String dateStr = null;
		String imageURL = null;
		String onClickURL = null;
		if (selectedFiles.size() == 1) {
			// If there is only one item selected
			for (Widget w : selectedFiles) {
				FileWidget fw = ((FileWidget)w);
				title = fw.getName();
				onClickURL = fw.getURL();
				imageURL = fw.getImageUrl();
				firstLine = Constants.isFolderMime(fw.getMime()) ? "" : getFormatedSize(fw.getSize());
				secondLine = "";
				dateStr = getFormatedDate(fw.getLastUpdate());
			}
		} else {
			double size = 0;
			double date = 0;
			int foldersNumber = 0;
			int filesNumber = 0;
			// Go through all selected items to show their info
			for (Widget w : selectedFiles) {
				FileWidget fw = ((FileWidget)w);
				if (Constants.isFolderMime(fw.getMime())) {
					foldersNumber++;
				} else {
					filesNumber++;
					size += fw.getSize();
				}
				if (fw.getLastUpdate() > date) {
					date = fw.getLastUpdate();
				}
			}
			
			title = Constants.I18N.nItems(selectedFiles.size());
			firstLine = foldersNumber > 0 ? Constants.I18N.mFolders(foldersNumber) : "";
			secondLine = filesNumber > 0 ? Constants.I18N.nFilesSize(filesNumber, getFormatedSize(size)) : "";
			dateStr = getFormatedDate(date);
			imageURL = Resources.INSTANCE.multipleSelection().getSafeUri().asString();
		}
		return new SelectionInfo(title, firstLine, secondLine, dateStr, imageURL, onClickURL);
	}

	public void changeInfo(String folderName, List<FileNode> inFolder) {
		// No items selected, shows info of the whole folder
		String nameToShow;
		if (folderName == null || folderName.equals("")) {
			nameToShow = Constants.I18N.home();
			setImage(Resources.INSTANCE.home());
		} else {
			nameToShow = folderName;
			setImage(GWT.getHostPageBaseURL() + "server/generic-thumbnail/" + Constants.FOLDER_MIME1);
		}
		selectionNameLabel.setText(nameToShow);
		selectionNameLabel.setTitle(nameToShow);
		double size = 0;
		double date = 0;
		int foldersNumber = 0;
		int filesNumber = 0;
		for (FileNode fn : inFolder) {
			if (Constants.isFolderMime(fn.getMime())) {
				foldersNumber++;
			} else {
				filesNumber++;
				size += fn.getSize();
			}
			if (fn.getLastUpdate() > date) {
				date = fn.getLastUpdate();
			}
		}
		setFoldersAndFilesLabels(foldersNumber, filesNumber, size);
		dateLabel.setText(getFormatedDate(date));
	}
	
	private void setFoldersAndFilesLabels(int foldersNumber, int filesNumber, double size) {
		// m folders
		// n files (k Kb)
		if (foldersNumber > 0) {
			info1Label.setText(Constants.I18N.mFolders(foldersNumber));
		} else {
			info1Label.setText("");
		}
		
		if (filesNumber > 0) {
			info2Label.setText(Constants.I18N.nFilesSize(filesNumber, getFormatedSize(size)));
		} else {
			info2Label.setText("");
		}
	}

	private void setImage(String url) {
		setImage(new Image(url));
	}
	
	private void setImage(ImageResource res) {
		setImage(new Image(res));
	}
	
	private void setImage(Image imageToShow) {
		imageToShow.getElement().addClassName(style.center());
		thumbnailPanel.clear();
		thumbnailPanel.add(imageToShow);
	}
	
	private void setHyperLink(String imageUrl, String linkUrl) {
		Image imageToShow = new Image(imageUrl);
		imageToShow.getElement().addClassName(style.center());
		Hyperlink link = new Hyperlink("", linkUrl);
		link.getElement().getChild(0).appendChild(imageToShow.getElement()); 
		thumbnailPanel.clear();
		thumbnailPanel.add(link);
	}
	
	// To get the String representing the size in a readable way
	public static String getFormatedSize(double size) {
		if (size < Constants.MAX_SHOWING_SIZE) {
			// return String.format("%.2f Bytes", size); // not supported in gwt yet...
			StringBuilder ret = getFormatedDouble(size);
			ret.append(" Bytes");
			return ret.toString();
		}
		size /= 1024;
		if (size < Constants.MAX_SHOWING_SIZE) {
			// return String.format("%.2f Kb", size);
			StringBuilder ret = getFormatedDouble(size);
			ret.append(" Kb");
			return ret.toString();
		}
		size /= 1024;
		if (size < Constants.MAX_SHOWING_SIZE) {
			// return String.format("%.2f Mb", size);
			StringBuilder ret = getFormatedDouble(size);
			ret.append(" Mb");
			return ret.toString();
		}
		size /= 1024;
		if (size < Constants.MAX_SHOWING_SIZE) {
			// return String.format("%.2f Gb", size);
			StringBuilder ret = getFormatedDouble(size);
			ret.append(" Gb");
			return ret.toString();
		}
		size /= 1024;
		// return String.format("%.2f Tb", size);
		StringBuilder ret = getFormatedDouble(size);
		ret.append(" Tb");
		return ret.toString();
	}
	
	// Since String.format doesn't work for gwt...
	public static StringBuilder getFormatedDouble(double number) {
		return new StringBuilder(NumberFormat.getFormat("0.##").format(number));
	}
	
	public static String getFormatedDate(double ddate) {
		Date date = new Date((long) ddate);
		return ((long) ddate) == 0 ? "" :
			Constants.I18N.modifiedDate(DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT).format(date));
	}

}

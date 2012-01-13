package com.nublic.app.browser.web.client.UI;

import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.model.FileNode;

public class SelectionDetails extends Composite {
	private static SelectionDetailsUiBinder uiBinder = GWT.create(SelectionDetailsUiBinder.class);
	interface SelectionDetailsUiBinder extends UiBinder<Widget, SelectionDetails> {	}

	@UiField InfoStyle style;
	@UiField Label selectionNameLabel;
	@UiField VerticalPanel thumbnailPanel;
	@UiField Label sizeLabel;
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
		if (newSelection.size() == 1) {
			// If there is only one item selected
			for (Widget w : newSelection) {
				FileWidget fw = ((FileWidget)w);
				selectionNameLabel.setText(fw.getName());
				selectionNameLabel.setTitle(fw.getName());
				setImage(fw.getImage().getUrl());
				double size = fw.getMime().equals(Constants.FOLDER_MIME) ? 0 : fw.getSize(); // To make size of folders 0
				sizeLabel.setText(getFormatedSize(size));
				dateLabel.setText("" + fw.getLastUpdate());
			}
		} else {
			selectionNameLabel.setText(newSelection.size() + " items");
			selectionNameLabel.setTitle(newSelection.size() + " items");
			setImage(GWT.getHostPageBaseURL() + "server/generic-thumbnail/" + Constants.FOLDER_MIME);
			double size = 0;
			double date = 0;
			// Go through all selected items to show their info
			for (Widget w : newSelection) {
				FileWidget fw = ((FileWidget)w);
				double addSize = fw.getMime().equals(Constants.FOLDER_MIME) ? 0 : fw.getSize();
				size += addSize;
				if (fw.getLastUpdate() > date) {
					date = fw.getLastUpdate();
				}
			}
			sizeLabel.setText(getFormatedSize(size));
			dateLabel.setText("" + date);
		}
	}

	public void changeInfo(String folderName, List<FileNode> inFolder) {
		// No items selected, shows info of the whole folder
		selectionNameLabel.setText(folderName);
		selectionNameLabel.setTitle(folderName);
		setImage(GWT.getHostPageBaseURL() + "server/generic-thumbnail/" + Constants.FOLDER_MIME);
		double size = 0;
		double date = 0;
		for (FileNode fn : inFolder) {
			double addSize = fn.getMime().equals(Constants.FOLDER_MIME) ? 0 : fn.getSize();
			size += addSize;
			if (fn.getLastUpdate() > date) {
				date = fn.getLastUpdate();
			}
		}
		sizeLabel.setText(getFormatedSize(size));
		dateLabel.setText("" + date);
	}
	
	private void setImage(String url) {
		Image imageToShow = new Image(url);
		imageToShow.getElement().addClassName(style.center());
		thumbnailPanel.clear();
		thumbnailPanel.add(imageToShow);
	}
	
	// To get the String representing the size in a readable way
	public static String getFormatedSize(double size) {
		if (size < Constants.MAX_SHOWING_SIZE) {
			// return String.format("%.2f Bytes", size); // not supported in gwt yet...
			StringBuilder ret = getFormatedDouble(size, 0);
			ret.append(" Bytes");
			return ret.toString();
		}
		size /= 1024;
		if (size < Constants.MAX_SHOWING_SIZE) {
			// return String.format("%.2f Kb", size);
			StringBuilder ret = getFormatedDouble(size, 2);
			ret.append(" Kb");
			return ret.toString();
		}
		size /= 1024;
		if (size < Constants.MAX_SHOWING_SIZE) {
			// return String.format("%.2f Mb", size);
			StringBuilder ret = getFormatedDouble(size, 2);
			ret.append(" Mb");
			return ret.toString();
		}
		size /= 1024;
		if (size < Constants.MAX_SHOWING_SIZE) {
			// return String.format("%.2f Gb", size);
			StringBuilder ret = getFormatedDouble(size, 2);
			ret.append(" Gb");
			return ret.toString();
		}
		size /= 1024;
		// return String.format("%.2f Tb", size);
		StringBuilder ret = getFormatedDouble(size, 2);
		ret.append(" Tb");
		return ret.toString();
	}
	
	// Since String.format doesn't work for gwt...
	public static StringBuilder getFormatedDouble(double number, int decimals) {
		StringBuilder ret = new StringBuilder();
		ret.append(number);
		int index = ret.indexOf(".");
		if (index != -1) {
			if (decimals == 0) {
				ret.setLength(index);
			} else {
				if (ret.length() > index + 1 + decimals) {
					ret.setLength(index + 1 + decimals);
				}
			}
		}
		return ret;
	}

}

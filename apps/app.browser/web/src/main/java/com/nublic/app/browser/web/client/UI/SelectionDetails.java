package com.nublic.app.browser.web.client.UI;

import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.model.FileNode;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

public class SelectionDetails extends Composite {
	private static SelectionDetailsUiBinder uiBinder = GWT.create(SelectionDetailsUiBinder.class);
	interface SelectionDetailsUiBinder extends UiBinder<Widget, SelectionDetails> {	}

	@UiField Label selectionNameLabel;
	@UiField SimplePanel thumbnailPanel;
	
	public SelectionDetails() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void changeInfo(Set<Widget> newSelection) {
		if (newSelection.size() == 1) {
			for (Widget w : newSelection) {
				FileWidget fw = ((FileWidget)w);
				selectionNameLabel.setText(fw.getName());
				thumbnailPanel.setWidget(new Image(fw.getImage().getUrl()));
			}
		} else {
			selectionNameLabel.setText(newSelection.size() + " items");
			thumbnailPanel.setWidget(new Image(GWT.getHostPageBaseURL() + "server/generic-thumbnail/" + Constants.FOLDER_MIME));
		}
	}
	
	public void changeInfo(String folderName, List<FileNode> inFolder) {
		selectionNameLabel.setText(folderName);
		thumbnailPanel.setWidget(new Image(GWT.getHostPageBaseURL() + "server/generic-thumbnail/" + Constants.FOLDER_MIME));
	}

}

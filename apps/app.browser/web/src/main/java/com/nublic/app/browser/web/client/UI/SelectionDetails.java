package com.nublic.app.browser.web.client.UI;

import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SelectionDetails extends Composite {
	private static SelectionDetailsUiBinder uiBinder = GWT.create(SelectionDetailsUiBinder.class);
	interface SelectionDetailsUiBinder extends UiBinder<Widget, SelectionDetails> {	}

	@UiField Label selectionNameLabel;
	
	public SelectionDetails() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void changeInfo(Set<FileWidget> newSelection) {
		if (newSelection.size() == 1) {
			
		} else {
			
		}
	}
	
	public void changeInfo(String folderName, Set<FileWidget> inFolder) {
		// TODO: por aqui!
	}

}

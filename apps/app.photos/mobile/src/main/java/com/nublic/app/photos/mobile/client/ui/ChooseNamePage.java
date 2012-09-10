package com.nublic.app.photos.mobile.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.gwtmobile.ui.client.page.Page;
import com.gwtmobile.ui.client.widgets.Button;
import com.gwtmobile.ui.client.widgets.TextBox;

public class ChooseNamePage extends Page {
	private static ChooseNamePageUiBinder uiBinder = GWT.create(ChooseNamePageUiBinder.class);
	interface ChooseNamePageUiBinder extends UiBinder<Widget, ChooseNamePage> { }

	@UiField Label title;
	@UiField Button okButton;
	@UiField TextBox nameBox;

	NameType type;
	
	public ChooseNamePage(NameType type) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.type = type;
		
		switch (type) {
		case NEW_ALBUM:
			title.setText("Enter new album name");
			break;
		}
	}
	
	@UiHandler("okButton")
	public void onClickOk(ClickEvent e) {
		switch (type) {
		case NEW_ALBUM:
			createNewAlbum(nameBox.getText());
			break;
		}
	}

	private void createNewAlbum(String albumTitle) {
		// TODO Auto-generated method stub
		
	}

}

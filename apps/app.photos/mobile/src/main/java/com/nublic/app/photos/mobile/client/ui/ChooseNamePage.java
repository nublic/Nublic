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
import com.nublic.app.photos.common.model.CallbackOneAlbum;
import com.nublic.app.photos.common.model.PhotosModel;

public class ChooseNamePage extends Page {
	private static ChooseNamePageUiBinder uiBinder = GWT.create(ChooseNamePageUiBinder.class);
	interface ChooseNamePageUiBinder extends UiBinder<Widget, ChooseNamePage> { }

	@UiField Label title;
	@UiField Button okButton;
	@UiField TextBox nameBox;

	NameType type;
	Object param;
	
	public ChooseNamePage(NameType type) {
		this(type, null);
	}
	
	public ChooseNamePage(NameType type, Object param) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.type = type;
		this.param = param;
		
		switch (type) {
		case NEW_ALBUM:
			title.setText("Enter new album name");
			break;
		case RENAME_PICURE:
			title.setText("Enter picture new name");
			break;
		}
	}

	@UiHandler("okButton")
	public void onClickOkButton(ClickEvent e) {
		switch (type) {
		case NEW_ALBUM:
			createNewAlbum(nameBox.getText());
			break;
		case RENAME_PICURE:
			renamePicture((Long)param, nameBox.getText());
			break;
		}
	}

	private void createNewAlbum(String albumTitle) {
		PhotosModel.get().newAlbum(albumTitle, new CallbackOneAlbum() {
			@Override
			public void list(long id, String name) {
				MainUi.INSTANCE.addNewAlbum(id, name);
				goBack(null);
			}
			@Override
			public void error() {
				goBack(null);
			}
		});
	}
	
	private void renamePicture(Long id, String text) {
		PhotosModel.get().changePhotoTitle(id, text);
		goBack(text);
	}

}

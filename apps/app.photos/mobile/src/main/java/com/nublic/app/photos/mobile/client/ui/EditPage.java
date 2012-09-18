package com.nublic.app.photos.mobile.client.ui;

import java.util.ArrayList;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.gwtmobile.ui.client.event.SelectionChangedEvent;
import com.gwtmobile.ui.client.event.SelectionChangedHandler;
import com.gwtmobile.ui.client.page.Page;
import com.gwtmobile.ui.client.widgets.Button;
import com.gwtmobile.ui.client.widgets.CheckBox;
import com.gwtmobile.ui.client.widgets.CheckBoxGroup;
import com.gwtmobile.ui.client.widgets.HeaderPanel;
import com.nublic.app.photos.common.model.CallbackListOfAlbums;
import com.nublic.app.photos.common.model.PhotoInfo;
import com.nublic.app.photos.common.model.PhotosModel;
import com.nublic.util.gwt.LocationUtil;

public class EditPage extends Page {
	private static EditPageUiBinder uiBinder = GWT.create(EditPageUiBinder.class);
	interface EditPageUiBinder extends UiBinder<Widget, EditPage> { }
	
	@UiField Label nameLabel;
	@UiField Label dateLabel;
	@UiField Image thumbnail;
	@UiField Button renameButton;
	@UiField HeaderPanel header;
	@UiField CheckBoxGroup checkGroup;
	PhotoInfo info;
	ArrayList<Long> albumIdList = new ArrayList<Long>();

	public EditPage(PhotoInfo info) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.info = info;
		
		nameLabel.setText(info.getTitle());
		DateTimeFormat formatter = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_FULL);
//		dateLabel.setText(Constants.I18N.takenOn(formatter.format(current.getDate())));
		dateLabel.setText(formatter.format(info.getDate()));
		thumbnail.setUrl(LocationUtil.encodeURL(GWT.getHostPageBaseURL() + "server/thumbnail/" + info.getId() + ".png"));
		addAlbumsCheckBoxes();
		
		header.setLeftButtonClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				goBack(nameLabel.getText());
			}
		});
	}
	
	private void addAlbumsCheckBoxes() {
		// Get albums containing the picture
		PhotosModel.get().albums(new CallbackListOfAlbums() {
			@Override
			public void list(final Map<Long, String> selectedAlbums) {
				// Get all albums and create checkboxes
				// mark them selected if they have the picture
				PhotosModel.get().albums(new CallbackListOfAlbums() {
					@Override
					public void list(Map<Long, String> albums) {
						for (Map.Entry<Long, String> album : albums.entrySet()) {
							albumIdList.add(album.getKey());
							CheckBox cb = new CheckBox();
							cb.setText(album.getValue());
							checkGroup.add(cb);
							if (selectedAlbums.containsKey(album.getKey())) {
								cb.setValue(true, false);
							}
						}
					}
					@Override
					public void error() {
						// nothing
					}
				});
			}
			@Override
			public void error() {
				// nothing
			}
		}, info.getId());

		checkGroup.addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionChangedEvent e) {
				int index = e.getSelection();
				CheckBox cb = (CheckBox)checkGroup.getWidget(index);
				boolean isSelected = cb.getValue();
				if (isSelected) {
					addToAlbum(albumIdList.get(index));
				} else {
					removeFromAlbum(albumIdList.get(index));
				}
			}
		});		
	}
	
	private void addToAlbum(Long albumId) {
		PhotosModel.get().addPhotoToAlbum(info.getId(), albumId);
	}
	
	private void removeFromAlbum(Long albumId) {
		PhotosModel.get().removePhotoFromAlbum(info.getId(), albumId);
	}

	@UiHandler("renameButton")
	public void onClickOkButton(ClickEvent e) {
		goTo(new ChooseNamePage(NameType.RENAME_PICURE, info.getId()));
	}
	
	@Override
	protected void onNavigateBack(Page from, Object object) {
		super.onNavigateBack(from, object);

		if (object != null) {
			nameLabel.setText((String) object);
		}
	}


}

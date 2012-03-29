package com.nublic.app.photos.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.photos.web.client.model.CallbackOneAlbum;
import com.nublic.app.photos.web.client.model.PhotosModel;

public class ShowAsCellsWidget extends Composite {

	private static ShowAsCellsWidgetUiBinder uiBinder = GWT.create(ShowAsCellsWidgetUiBinder.class);

	interface ShowAsCellsWidgetUiBinder extends UiBinder<Widget, ShowAsCellsWidget> {
	}
	
	@UiField HorizontalPanel titlePanel;
	@UiField Label titleLabel;
	@UiField FlowPanel mainPanel;
	
	long id;

	public ShowAsCellsWidget(long id) {
		initWidget(uiBinder.createAndBindUi(this));
		
		// Set title label
		this.id = id;
		if (id == -1) {
			titleLabel.setText("All albums");
		} else {
			PhotosModel.get().album(id, new CallbackOneAlbum() {
				@Override
				public void list(long id, String name) {
					titleLabel.setText(name);
				}
				@Override
				public void error() {
					titleLabel.setText("Unknown album");
				}
			});
		}
		
		// Set inner widgets
	}

}

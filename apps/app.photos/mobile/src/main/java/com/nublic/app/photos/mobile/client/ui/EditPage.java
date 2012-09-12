package com.nublic.app.photos.mobile.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.gwtmobile.ui.client.page.Page;
import com.gwtmobile.ui.client.widgets.Button;
import com.nublic.app.photos.common.model.PhotoInfo;
import com.nublic.util.gwt.LocationUtil;

public class EditPage extends Page {
	private static EditPageUiBinder uiBinder = GWT.create(EditPageUiBinder.class);
	interface EditPageUiBinder extends UiBinder<Widget, EditPage> { }
	
	@UiField Label nameLabel;
	@UiField Label dateLabel;
	@UiField Image thumbnail;
	@UiField Button renameButton;

	public EditPage(PhotoInfo info) {
		initWidget(uiBinder.createAndBindUi(this));
		
		nameLabel.setText(info.getTitle());
		DateTimeFormat formatter = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_FULL);
//		dateLabel.setText(Constants.I18N.takenOn(formatter.format(current.getDate())));
		dateLabel.setText(formatter.format(info.getDate()));
		thumbnail.setUrl(LocationUtil.encodeURL(GWT.getHostPageBaseURL() + "server/thumbnail/" + info.getId() + ".png"));
	}

}

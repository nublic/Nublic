package com.nublic.app.photos.mobile.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
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
import com.nublic.app.photos.common.model.PhotoInfo;
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

		CheckBox cb = new CheckBox();
		cb.setText("Album playa");
		checkGroup.add(cb);

		CheckBox cb2 = new CheckBox();
		cb2.setText("Album montaña");
		checkGroup.add(cb2);

		checkGroup.addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionChangedEvent e) {
				Window.alert("Change in " + e.getSelection());
			}
		});
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

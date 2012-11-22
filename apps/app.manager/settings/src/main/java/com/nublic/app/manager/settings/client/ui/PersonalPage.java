package com.nublic.app.manager.settings.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.manager.settings.client.Model;
import com.nublic.app.manager.settings.client.comm.User;
import com.nublic.app.manager.settings.client.comm.UserMessageCallback;
import com.nublic.util.widgets.EditableLabel;

public class PersonalPage extends Composite {
	private static PersonalPageUiBinder uiBinder = GWT.create(PersonalPageUiBinder.class);
	interface PersonalPageUiBinder extends UiBinder<Widget, PersonalPage> { }

	@UiField EditableLabel shownName;
	@UiField Label systemName;

	public PersonalPage() {
		initWidget(uiBinder.createAndBindUi(this));

		Model.INSTANCE.getUserInfo(new UserMessageCallback() {
			@Override
			public void onUserMessage(User u) {
				shownName.setText(u.getShownName());
				systemName.setText(u.getSystemName());
			}
		});
	}

	@UiHandler("shownName")
	void onShownNameValueChange(ValueChangeEvent<String> event) {
		Model.INSTANCE.setUserShownName(event.getValue());
	}
	
}

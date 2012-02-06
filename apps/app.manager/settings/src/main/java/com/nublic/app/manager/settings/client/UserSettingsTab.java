package com.nublic.app.manager.settings.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.manager.settings.client.comm.NublicUser;
import com.nublic.util.gwt.LocationUtil;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;
import com.nublic.util.widgets.EditableLabel;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;

public class UserSettingsTab extends Composite {

	private static UserSettingsTabUiBinder uiBinder = GWT.create(UserSettingsTabUiBinder.class);

	interface UserSettingsTabUiBinder extends UiBinder<Widget, UserSettingsTab> {
	}
	
	@UiField EditableLabel name;
	@UiField Label username;

	public UserSettingsTab() {
		initWidget(uiBinder.createAndBindUi(this));
		
		SequenceHelper.sendJustOne(new Message() {
			@Override
			public String getURL() {
				return LocationUtil.getHostBaseUrl() + "manager/server/user-info";
			}
			@Override
			public void onSuccess(Response response) {				
				if (response.getStatusCode() == Response.SC_OK) {
					// When the call is successful
					String text = response.getText();
					NublicUser userInfo = JsonUtils.safeEval(text);
					name.setText(userInfo.getShownName());
					username.setText(userInfo.getUserName());
				}
			}
			@Override
			public void onError() {
				// Do nothing
			}
		}, RequestBuilder.GET);
	}

	@UiHandler("name")
	void onNameValueChange(ValueChangeEvent<String> event) {
		Message m = new Message() {
			@Override
			public String getURL() {
				return LocationUtil.getHostBaseUrl() + "manager/server/user-info";
			}
			@Override
			public void onSuccess(Response response) {				
				// Do nothing
			}
			@Override
			public void onError() {
				// Do nothing
			}
		};
		m.addParam("name", event.getValue());
		SequenceHelper.sendJustOne(m, RequestBuilder.PUT);
	}
}

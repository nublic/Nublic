package com.nublic.app.manager.web.settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class DeviceSettingsTab extends Composite {

	private static DeviceSettingsTabUiBinder uiBinder = GWT.create(DeviceSettingsTabUiBinder.class);

	interface DeviceSettingsTabUiBinder extends UiBinder<Widget, DeviceSettingsTab> {
	}

	public DeviceSettingsTab() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}

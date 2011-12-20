package com.nublic.app.browser.web.client.devices;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.browser.web.client.model.BrowserModel;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.Message;

public class DeviceMessage extends Message {
	DevicesManager manager;
	BrowserModel model;

	public DeviceMessage(DevicesManager manager, BrowserModel model) {
		this.manager = manager;
		this.model = model;
	}

	@Override
	public String getURL() {
		return URL.encode(GWT.getHostPageBaseURL() + "server/devices");
	}

	@Override
	public void onSuccess(Response response) {
		JsArray <JSDevice> jsDevicesList = null;

		if (Response.SC_OK == response.getStatusCode()) {
			// When the call is successful
			String text = response.getText();
			jsDevicesList = JsonUtils.safeEval(text);
			// Update the tree with the information of folders
			if (jsDevicesList == null) {
				ErrorPopup.showError("The request could not be processed");
			} else {
				manager.clearDevices();
				for (int i = 0 ; i < jsDevicesList.length() ; i++) {
					JSDevice jsDevice = jsDevicesList.get(i);
					manager.addDevice(new Device(jsDevice.getId(),
												 jsDevice.getKind(),
												 jsDevice.getName(),
												 jsDevice.getOwner()));
				}
				manager.createRootTree(model);
				model.fireUpdateHandlers(model.getFolderTree());
			}
		} else {
			ErrorPopup.showError("The request could not be processed");
		}
	}

	@Override
	public void onError() {
		ErrorPopup.showError("Server unavailable");
	}

}

package com.nublic.app.browser.web.client.devices;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.http.client.RequestBuilder;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.util.messages.SequenceHelper;

public class DevicesManager {
	List<Device> devicesList = new ArrayList<Device>();

	public void updateDevices() {
		DeviceMessage m = new DeviceMessage(this);
		SequenceHelper.sendJustOne(m, RequestBuilder.GET);
	}

	// To be called mainly by DeviceMessage
	public void addDevice(Device d) {
		devicesList.add(d);
	}

	public void removeDevice(Device d) {
		devicesList.remove(d);
	}

	public void clearDevices() {
		devicesList.clear();
	}

	// To transform real paths to mock ones and viceversa
	public String getRealPath(String mockPath) {
		// Converts paths of the form "dispositive_name/..." to "dispositive_kind/id/.."
		String splitPath[] = mockPath.split("/", 2);
		if (splitPath[0].equals(Constants.NUBLIC_ONLY)) {
			return mockPath;
		} else {
			StringBuilder realPath = new StringBuilder();
			for (Device d : devicesList) {
				if (d.getName().equals(splitPath[0])) {
					realPath.append(d.getKind().getPathName());
					realPath.append("/");
					realPath.append(d.getId());
					realPath.append("/");
					realPath.append(splitPath[1]);
					return realPath.toString();
				}
			}
			// This should never happen
			return null;
		}
	}
	
	public String getMockPath(String realPath) {
		// Convert paths of the form "dispositive_kind/id/.." to "dispositive_name/..."
		String splitPath[] = realPath.split("/", 3);
		
		if (splitPath[0].equals(Constants.NUBLIC_ONLY) || splitPath.length < 2) {
			return realPath;
		} else {
			for (Device d : devicesList) {
				if (d.getKind() == DeviceKind.parse(splitPath[0])
						&& d.getId() == Integer.valueOf(splitPath[1])) {
					return d.getName() + splitPath[2];
				}
			}
			// This should never happen
			return null;
		}
	}
}

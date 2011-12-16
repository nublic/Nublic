package com.nublic.app.browser.web.client.devices;

import com.nublic.app.browser.web.client.Constants;

public enum DeviceKind {
	MIRROR(Constants.KIND_MIRROR),
	SYNCED(Constants.KIND_SYNCED),
	MEDIA(Constants.KIND_MEDIA);
	
	String msg;
	
	private DeviceKind(String msg) {
		this.msg = msg;
	}
	
	public String getMessage() {
		return this.msg;
	}
	
	public String getPathName() {
		if (msg.equals(Constants.KIND_MIRROR)) {
			return Constants.KIND_MIRROR_FOLDER;
		} else if (msg.equals(Constants.KIND_SYNCED)) {
			return Constants.KIND_SYNCED_FOLDER;
		} else if (msg.equals(Constants.KIND_MEDIA)) {
			return Constants.KIND_MEDIA_FOLDER;
		} else {
			return null;
		}
	}
	
	public static DeviceKind parse(String s) {
		for (DeviceKind k : DeviceKind.values()) {
			if (s.equals(k.getMessage())) {
				return k;
			}
		}
		return null;
	}
}

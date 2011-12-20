package com.nublic.app.browser.web.client.devices;

import com.nublic.app.browser.web.client.Constants;

public enum DeviceKind {
	MIRROR(Constants.KIND_MIRROR, Constants.KIND_MIRROR_FOLDER),
	SYNCED(Constants.KIND_SYNCED, Constants.KIND_SYNCED_FOLDER),
	MEDIA(Constants.KIND_MEDIA, Constants.KIND_MEDIA_FOLDER);
	
	String msg;
	String folderKind;
	
	private DeviceKind(String msg, String folderKind) {
		this.msg = msg;
		this.folderKind = folderKind;
	}
	
	public String getMessage() {
		return this.msg;
	}
	
	public String getPathName() {
		return folderKind;
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

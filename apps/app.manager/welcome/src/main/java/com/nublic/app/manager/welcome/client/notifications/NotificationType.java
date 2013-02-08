package com.nublic.app.manager.welcome.client.notifications;

import com.google.gwt.resources.client.ImageResource;
import com.nublic.app.manager.welcome.client.Resources;

public enum NotificationType {
	INFO(Resources.INSTANCE.info()),
	ATTENTION(Resources.INSTANCE.attention()),
	POWER(Resources.INSTANCE.power()),
	USER(Resources.INSTANCE.user()),
	PASWORD(Resources.INSTANCE.password());
	
	ImageResource icon;
	
	private NotificationType(ImageResource ir) {
		icon = ir;
	}
	
	public ImageResource getResource() {
		return icon;
	}
}

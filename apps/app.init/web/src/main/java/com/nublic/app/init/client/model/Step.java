package com.nublic.app.init.client.model;

import com.google.gwt.resources.client.ImageResource;
import com.nublic.app.init.client.Constants;
import com.nublic.app.init.client.Resources;

public enum Step {
	WELCOME("", null),
	USERS(Constants.I18N.userStep(), Resources.INSTANCE.users()),
	MASTER_USER(Constants.I18N.masterUserStep(), Resources.INSTANCE.master()),
	NET_CONFIG(Constants.I18N.netConfigStep(), Resources.INSTANCE.network()),
	NAME(Constants.I18N.nameStep(), Resources.INSTANCE.name()),
	FINISHED("", null);
	
	String name;
	ImageResource image;
	
	private Step(String name, ImageResource image) {
		this.name = name;
		this.image = image;
	}
	
	public String getName() {
		return name;
	}
	
	public ImageResource getImage() {
		return image;
	}
}

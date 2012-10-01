package com.nublic.app.init.client.model;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.init.client.Constants;
import com.nublic.app.init.client.Resources;
import com.nublic.app.init.client.ui.WelcomePage;

public enum Step {
	WELCOME("", null, new WelcomePage()),
	USERS(Constants.I18N.userStep(), Resources.INSTANCE.users(), new WelcomePage()),
	MASTER_USER(Constants.I18N.masterUserStep(), Resources.INSTANCE.master(), new Widget()),
	NET_CONFIG(Constants.I18N.netConfigStep(), Resources.INSTANCE.network(), new Widget()),
	NAME(Constants.I18N.nameStep(), Resources.INSTANCE.name(), new Widget()),
	FINISHED("", null, new Widget());
	
	String name;
	ImageResource image;
	Widget uiWidget;
	
	private Step(String name, ImageResource image, Widget uiWidget) {
		this.name = name;
		this.image = image;
		this.uiWidget = uiWidget;
	}
	
	public String getName() {
		return name;
	}
	
	public ImageResource getImage() {
		return image;
	}

	public Widget getUiWidget() {
		return uiWidget;
	}
}

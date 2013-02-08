package com.nublic.app.manager.welcome.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Resources extends ClientBundle {
	public static final Resources INSTANCE =  GWT.create(Resources.class);
	
	@Source("images/attention.png")
	ImageResource attention();
	
	@Source("images/info.png")
	ImageResource info();
	
	@Source("images/password.png")
	ImageResource password();
	
	@Source("images/power.png")
	ImageResource power();
	
	@Source("images/user.png")
	ImageResource user();
}
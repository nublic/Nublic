package com.nublic.app.manager.settings.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources used by the entire application.
 */
public interface Resources extends ClientBundle {
	public static final Resources INSTANCE =  GWT.create(Resources.class);
	
	@Source("images/smallperson.png")
	ImageResource person();
	
	@Source("images/password.png")
	ImageResource password();
	
	@Source("images/trash.png")
	ImageResource trash();
	
	@Source("images/attention.png")
	ImageResource attention();
	
	@Source("images/eye.png")
	ImageResource eye();
}
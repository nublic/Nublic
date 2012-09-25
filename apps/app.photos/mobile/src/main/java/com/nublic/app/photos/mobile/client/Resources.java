package com.nublic.app.photos.mobile.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources used by the entire application.
 */
public interface Resources extends ClientBundle {
	public static final Resources INSTANCE =  GWT.create(Resources.class);
	
	@Source("images/loading.gif")
	ImageResource loading();

}
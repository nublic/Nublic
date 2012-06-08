package com.nublic.app.market.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources used by the entire application.
 */
public interface Resources extends ClientBundle {
	public static final Resources INSTANCE =  GWT.create(Resources.class);
	
	@Source("images/next.png")
	ImageResource next();
	
	@Source("images/prev.png")
	ImageResource prev();

	@Source("images/next2.png")
	ImageResource next2();

	@Source("images/prev2.png")
	ImageResource prev2();
}
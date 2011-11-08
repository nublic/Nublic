package com.nublic.app.browser.web.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources used by the entire application.
 */
public interface Resources extends ClientBundle {
	@Source("images/up.png")
	ImageResource up();
	
	@Source("images/down.png")
	ImageResource down();
}
package com.nublic.app.browser.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources used by the entire application.
 */
public interface Resources extends ClientBundle {
	public static final Resources INSTANCE =  GWT.create(Resources.class);
	
	@Source("images/up.png")
	ImageResource up();
	
	@Source("images/down.png")
	ImageResource down();
	
	@Source("images/download.png")
	ImageResource download();
	
	@Source("images/view.png")
	ImageResource view();
	
	@Source("images/multiple-selection.png")
	ImageResource multipleSelection();
}
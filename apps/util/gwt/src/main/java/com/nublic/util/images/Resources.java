package com.nublic.util.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources used by the entire application.
 */
public interface Resources extends ClientBundle {
	public static final Resources INSTANCE =  GWT.create(Resources.class);
	
	@Source("check.png")
	ImageResource check();
	
	@Source("cross.png")
	ImageResource cross();
	
	@Source("loading.gif")
	ImageResource loading();
}

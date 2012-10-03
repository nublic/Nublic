package com.nublic.app.init.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources used by the entire application.
 */
public interface Resources extends ClientBundle {
	public static final Resources INSTANCE =  GWT.create(Resources.class);
	
	@Source("images/users.png")
	ImageResource users();
	
	@Source("images/master.png")
	ImageResource master();
	
	@Source("images/network.png")
	ImageResource network();
	
	@Source("images/name.png")
	ImageResource name();
	
	@Source("images/person.png")
	ImageResource person();
	
	@Source("images/check.png")
	ImageResource check();
	
	@Source("images/cross.png")
	ImageResource cross();
	
	@Source("images/loading.gif")
	ImageResource loading();
}

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
	
	@Source("images/personsmall.png")
	ImageResource personsmall();
	
	@Source("images/checkbig.png")
	ImageResource checkbig();
	
	@Source("images/attention.png")
	ImageResource attention();
	
	@Source("images/info.png")
	ImageResource info();
	
	@Source("images/link.png")
	ImageResource link();
	
	@Source("images/config.png")
	ImageResource config();
	
	@Source("images/sparkleshare.png")
	ImageResource sparkleshare();
	
	@Source("images/logo.png")
	ImageResource logo();
	
	@Source("images/multimedia.png")
	ImageResource multimedia();
}

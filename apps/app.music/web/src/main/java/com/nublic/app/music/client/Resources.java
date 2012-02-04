package com.nublic.app.music.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources used by the entire application.
 */
public interface Resources extends ClientBundle {
	public static final Resources INSTANCE =  GWT.create(Resources.class);
	
	@Source("images/play_mini.png")
	ImageResource playMini();
	
	@Source("images/edit.png")
	ImageResource edit();
	
	@Source("images/add_at_end.png")
	ImageResource addAtEnd();
	
	@Source("images/save.png")
	ImageResource save();
	
	@Source("images/plus.png")
	ImageResource plus();
	
	@Source("images/delete.png")
	ImageResource delete();
}
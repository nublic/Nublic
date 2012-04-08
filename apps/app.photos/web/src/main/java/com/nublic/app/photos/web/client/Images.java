package com.nublic.app.photos.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources used by the entire application.
 */
public interface Images extends ClientBundle {
	public static final Images INSTANCE =  GWT.create(Images.class);
	
	@Source("images/prev_photo.png")
	ImageResource prevPhoto();
	
	@Source("images/next_photo.png")
	ImageResource nextPhoto();
	
	@Source("images/delete_black.png")
	ImageResource deleteAlbumBlack();
	
	@Source("images/delete_white.png")
	ImageResource deleteAlbumWhite();
	
	@Source("images/grid.png")
	ImageResource showAsGrid();
	
	@Source("images/remove_files.png")
	ImageResource removeFiles();
	
	@Source("images/empty_album.png")
	ImageResource emptyAlbum();
	
	@Source("images/image.png")
	ImageResource image();
	
	@Source("images/new_album.png")
	ImageResource newAlbum();

}
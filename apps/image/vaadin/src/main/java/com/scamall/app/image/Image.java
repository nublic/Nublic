package com.scamall.app.image;

import java.io.File;
import java.io.IOException;

/**
 * Handles the model part of the image, its location in the file system and its thumbnails.
 * @author Cesar Navarro Estruch
 *
 */
public class Image {
	File resource;
	// Add the rest of fields here in the future
	
	public Image(File resource){
		this.resource = resource;
	}
	
	public File getResource() {
		return resource;
	}
	public void setResource(File resource) {
		this.resource = resource;
	}
	
	/**
	 * It creates a resized image using Im4java
	 * @param x The width of the new image
	 * @param y The height of the new image
	 * @return The new image
	 */
	public Image resize (int x, int y) {
		return this;
		//TODO: All
	}
}

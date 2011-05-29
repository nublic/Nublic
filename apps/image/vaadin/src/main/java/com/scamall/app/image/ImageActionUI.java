package com.scamall.app.image;

import com.vaadin.ui.Layout;

public interface ImageActionUI extends Layout{
	/***
	 * This method assigns the image to the actionUI.
	 * 
	 * The UI which implements this should refresh when this method
	 * is invoked
	 * @param image The assigned image
	 */
	public void setImage(Image image);
}

package com.example.nublic_images;

import java.io.File;
import java.io.FileReader;

import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.terminal.FileResource;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Embedded;

/**
 * The view part of the image. It displays an Image, and supports a LayoutClickListener.
 * @author Cesar Navarro Estruch
 *
 */
public class ImageUI extends CssLayout {
	private Embedded image;
	private LayoutClickListener listener;
	private Image img;
	
	/**
	 * Initializes the image
	 * @param image
	 * @param layoutClickListener
	 */
	public ImageUI (Image image, LayoutClickListener layoutClickListener){
		img = image;
		listener = layoutClickListener;
		//Note: The rest of the code is in attach()
	}
	
	// This code is here because it couldn't be in the constructor, because I 
	 //needed getApplication, and it is available after doing attach()
	@Override
	public void attach(){
		super.attach();
		
		//it creates the embedded. It is what shows the image itself
		image = new Embedded("", new FileResource(img.getResource(), getApplication()));
		// this is a CSSLayout. I chose this because I think it 
		  //was the fastest, but it is not very important
		this.addComponent(image);
		this.addListener(listener);
	}
	
	/**
	 * It refreshes IamgeUI with a new image and a new layoutClickListener
	 * @param image The new Image
	 * @param layoutClickListener The new listener
	 */
	public void refresh(Image image, LayoutClickListener layoutClickListener){
		image.setResource(image.getResource());
		this.removeListener(listener);
		listener = layoutClickListener;
		this.addListener(listener);
	}
}

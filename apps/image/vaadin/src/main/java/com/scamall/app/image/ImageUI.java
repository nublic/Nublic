package com.scamall.app.image;

import java.io.File;

import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.terminal.FileResource;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Embedded;

/**
 * The view part of the image. It displays an Image, and supports a LayoutClickListener.
 * 
 * It is a CSSLayout. It was chosen because It should be the fastest, 
 * but it really does not matter.
 * 
 * @author Cesar Navarro Estruch
 *
 */
public class ImageUI extends CssLayout {

	private static final long serialVersionUID = 5038287151309275552L;
	
	private Embedded image;
	private ClickListener listener;
	private File img;
	private int index;
	
	/**
	 * Initializes the image, including the action done when receive a click.
	 * @param image
	 * @param clickListener
	 */
	public ImageUI (File image, int index, ClickListener clickListener){
		img = image;
		listener = clickListener;
		this.index = index;
		//Note: The rest of the code is in attach()
	}
	
	// This code is here because it couldn't be in the constructor. It is 
	// needed a getApplication, and it is available after doing attach()
	@Override
	public void attach(){
		super.attach();
		
		//it creates the embedded. It is what shows the image itself
		image = new Embedded("", new FileResource(img, getApplication()));
		image.addListener(listener);
		this.addComponent(image);
	}
	
	/**
	 * It refreshes ImageUI with a new image and a new layoutClickListener
	 * @param image The new Image
	 * @param layoutClickListener The new listener
	 */
	public void refresh(File img, ClickListener clickListener){
		this.removeAllComponents();
		listener = clickListener;
		
		this.img = img;
		
		//it creates the embedded. It is what shows the image itself
		image = new Embedded("", new FileResource(img, getApplication()));
		image.addListener(listener);

		this.addComponent(image);
		
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
}

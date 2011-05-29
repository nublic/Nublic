package com.scamall.app.image;

import java.util.ArrayList;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class ImageActionPanel extends VerticalLayout {
	ArrayList<ImageActionUI> components = new ArrayList<ImageActionUI>();
	
	/**
	 * It adds a component to the panel. The component must be a 
	 * {@link ImageActionUI}, so it is able to get the current image.
	 * @param component The component added to the action panel
	 */
	public void addActionComponent(ImageActionUI component){
		components.add(component);
		this.addComponent(component);
	}

	/**
	 * It assigns a new image to the panel
	 * @param image The assigned image
	 */
	public void setImage(Image image){
		for (int i = 0; i < components.size(); i++) {
			components.get(i).setImage(image);
		}
	}
}

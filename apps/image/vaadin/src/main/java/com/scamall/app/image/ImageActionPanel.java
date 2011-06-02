package com.scamall.app.image;

import java.util.ArrayList;
import com.vaadin.ui.VerticalLayout;

public class ImageActionPanel extends VerticalLayout {
	
	private static final long serialVersionUID = -8994613686772179409L;
	
	ArrayList<ImageActionUI> components = new ArrayList<ImageActionUI>();
	 
	/** Action Panel for the ImageView layout
	 * 
	 * @param currentState
	 */
	public ImageActionPanel(SingleImageView currentState) {
		// TODO Must replace ImageView with the actual State of the application
	}
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

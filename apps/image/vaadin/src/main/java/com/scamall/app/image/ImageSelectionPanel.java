package com.scamall.app.image;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.HorizontalLayout;

/**
 * This is the panel under the image in ImageView, which allows the user to navigate through the images.
 * @author Cesar Navarro Estruch
 *
 */
public class ImageSelectionPanel extends HorizontalLayout {
	BeanItemContainer<Image> images;
	int current;
	
	/**
	 * It is initialized taking with the list of images and the current
	 * @param images All the images that will be shown
	 * @param currentImage The current image
	 */
	public ImageSelectionPanel(BeanItemContainer<Image> images, int currentImage){
		this.images= images;
		this.current = currentImage;
		
		// This implementation does not work properly, it is just a test
		ImageUI [] thumbnails = new ImageUI[images.size()];
		for (int i=current;i<2;i++){
			thumbnails[i] = new ImageUI(images.getItem(images.getIdByIndex(i)), new LayoutClickListener() {
				public void layoutClick(LayoutClickEvent event) {					
				}
			});
			
			thumbnails[i].setHeight("80px");
			addComponent(thumbnails[i]);
		}
		//TODO: It must contain every image, and must have a navigation system to see the images that are not visible
		//TODO: Each thumbnail must have the right size
		//TODO: The thumbnails must be done with the Image interface, when it is ready
		
	}
	
}

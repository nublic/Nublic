package com.scamall.app.image;


import java.util.ArrayList;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

/**
 * The image screen. {@link http://develop.nublic.com/trac/wiki/BocetosImagen#Visualizacióndeunaimagen}
 * @author Cesar Navarro Estruch
 *
 */
public class ImageView extends HorizontalLayout {
	private BeanItem<Image> image;
	private int currentImageNumb;
	private ImageActionPanel actionPanel;
	private ImageUI mainImage;
	private ImageSelectionPanel selectionPanel;
	//imageNavigation includes the image and the navigation
	private VerticalLayout imageNavigation = new VerticalLayout();
	private BeanItemContainer<Image> images;
	
	/**
	 * It creates the imageView with a list of images, and which in this list you want
	 *  to display. The images are displayed in the ImageSelectionPanel.
	 * @param images The list of images
	 * @param currentImage In which image you start
	 */
	public ImageView(BeanItemContainer<Image> images, int currentImage){
		currentImageNumb = currentImage;
		this.images = images;
		image = images.getItem(images.getIdByIndex(currentImageNumb));

		// Left part
		this.addComponent(imageNavigation);
		
		mainImage = new ImageUI(image, new LayoutClickListener() {
			public void layoutClick(LayoutClickEvent event) {
				ImageView i = ((ImageView)event.getComponent().getParent().getParent().getParent());
				i.refresh(i.getNumberCurrentImage()+1);
			}
		});
		
		imageNavigation.addComponent(mainImage);
		
        selectionPanel = new ImageSelectionPanel(images, currentImage);
		imageNavigation.addComponent(selectionPanel);
		imageNavigation.setWidth("600pt");
		
		// Right panel
		actionPanel = new ImageActionPanel(this);
		actionPanel.setWidth("30%");
		this.addComponent(actionPanel);
		
		//TODO: Fix the size of every part
	}
	
	/**
	 * It changes the current image for another from the list.
	 * @param newImage The index of the new image
	 */
	public void refresh(int newImage){
		currentImageNumb = newImage;
		image = images.getItem(images.getIdByIndex(newImage));
		
		mainImage.refresh(image.getBean(), new LayoutClickListener() {
			public void layoutClick(LayoutClickEvent event) {
				ImageView i = ((ImageView)event.getComponent().getParent());
				i.refresh(i.getNumberCurrentImage()+1);		
			}
		});
		actionPanel.setImage(image.getBean());
		
		//TODO: Make sure that everything refreshes properly
	}
	
	public int getNumberCurrentImage(){
		return currentImageNumb;
	}
	
}

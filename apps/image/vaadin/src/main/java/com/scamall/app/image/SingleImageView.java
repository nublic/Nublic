package com.scamall.app.image;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

/**
 * The image screen. {@link http://develop.nublic.com/trac/wiki/BocetosImagen#Visualizacióndeunaimagen}
 * @author Cesar Navarro Estruch
 *
 */
public class SingleImageView extends HorizontalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2959876785796452524L;

	private SingleImageWindowState state;
	
	private ImageActionPanel actionPanel;
	private ImageUI mainImage;
	private ImageSelectionPanel selectionPanel;
	//imageNavigation includes the image and the navigation
	private VerticalLayout imageNavigation = new VerticalLayout();
	
	/**
	 * It creates the imageView with a list of images, and which in this list you want
	 *  to display. The images are displayed in the ImageSelectionPanel.
	 * @param images The list of images
	 * @param currentImage In which image you start
	 */
	public SingleImageView(SingleImageWindowState state){
		this.state = state;
		
		// Left part
		this.addComponent(imageNavigation);
		
		mainImage = new ImageUI(state.getCurrentBeanImage(), new LayoutClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 9074849005391863211L;

			public void layoutClick(LayoutClickEvent event) {
				SingleImageView i = ((SingleImageView)event.getComponent().getParent().getParent().getParent());
				i.refresh(i.getNumberCurrentImage()+1);
			}
		});
		
		imageNavigation.addComponent(mainImage);
		
        selectionPanel = new ImageSelectionPanel(state.getListImages(), state.getCurrentPosition());
		imageNavigation.addComponent(selectionPanel);
		imageNavigation.setWidth("600pt");
		
		// Right panel
		actionPanel = new ImageActionPanel(this);
		actionPanel.setWidth("30%");
		this.addComponent(actionPanel);
		
		//TODO: Fix the size of every part
	}
	
	/**
	 * It changes the current image from the same the list.
	 * @param newImage The index of the new image
	 */
	public void refresh(int newImage){
		state.setCurrentPosition(newImage);
		Image image = state.getListImages().getIdByIndex(newImage);
		
		mainImage.refresh(image, new LayoutClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -8522649092211142180L;

			public void layoutClick(LayoutClickEvent event) {
				SingleImageView i = ((SingleImageView)event.getComponent().getParent());
				i.refresh(i.getNumberCurrentImage()+1);
			}
		});
		actionPanel.setImage(image);
		
		//TODO: Make sure that everything refreshes properly
	}
	
	public int getNumberCurrentImage(){
		return state.getCurrentPosition();
	}
	
}

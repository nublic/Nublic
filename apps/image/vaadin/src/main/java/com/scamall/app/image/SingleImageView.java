package com.scamall.app.image;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

/**
 * The image screen. {@link http
 * ://develop.nublic.com/trac/wiki/BocetosImagen#Visualizacióndeunaimagen}
 * 
 * @author Cesar Navarro Estruch
 * 
 */
public class SingleImageView extends HorizontalLayout {

	private static final long serialVersionUID = 2959876785796452524L;

	private SingleImageWindowState state;

	private ImageActionPanel actionPanel;
	private ImageUI mainImage;
	private ImageSelectionPanel selectionPanel;
	// imageNavigation includes the image and the navigation
	private VerticalLayout imageAndNavigation;

	/**
	 * It creates the imageView with a list of images, and which in this list
	 * you want to display. The images are displayed in the ImageSelectionPanel.
	 * 
	 * @param images
	 *            The list of images
	 * @param currentImage
	 *            In which image you start
	 */
	public SingleImageView(SingleImageWindowState state) {
		this.state = state;
		state.setView(this);

		refresh();
	}

	/**
	 * It changes the current image from the same the list.
	 * 
	 * @param newImage
	 *            The index of the new image
	 */
	public void refresh(int newImage) {
		state.setCurrentPosition(newImage);

		refresh();
	}

	/**
	 * Refresh all the data from the State
	 * 
	 */
	public void refresh() {
		this.removeAllComponents();
		// Left part
		this.imageAndNavigation = new VerticalLayout();

		mainImage = new ImageUI(state.getCurrentBeanImage().getBean()
				.getResourceNormalSize(), state.getCurrentPosition(), new ClickListener() {

			private static final long serialVersionUID = 9074849005391863211L;

			public void click(ClickEvent event) {
				((SingleImageView) ((ImageWindow) (event.getComponent()
						.getWindow())).getView()).getState().nextImage();
			}
		});

		imageAndNavigation.addComponent(mainImage);

		selectionPanel = new ImageSelectionPanel(state.getListImages(),
				state.getCurrentPosition());
		imageAndNavigation.addComponent(selectionPanel);
		imageAndNavigation.setWidth("600pt");
		this.addComponent(imageAndNavigation);

		// Right panel
		actionPanel = new ImageActionPanel(this.getState());
		actionPanel.setWidth("30%");
		this.addComponent(actionPanel);
	}

	public int getNumberCurrentImage() {
		return state.getCurrentPosition();
	}

	public SingleImageWindowState getState() {
		return state;
	}

}

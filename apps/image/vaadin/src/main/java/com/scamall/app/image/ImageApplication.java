/*
 * 
 */
package com.scamall.app.image;

import com.vaadin.ui.Window;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class ImageApplication extends NublicApplication {

	@Override
	public void init() {
		Window mainWindow = new Window("Nublic_images Application");
		setMainWindow(mainWindow);
//		File imagesFileParent = new File("/var/nublic/Imágenes/Fondos");
		
		SingleImageWindowState state = new SingleImageWindowState();
		state.setListId("/var/nublic/Imágenes/Fondos");
		state.setCurrentPosition(0);
//		BeanItemContainer<Image> images = this.getImagesFromDirectory(imagesFileParent);

		mainWindow.addComponent(new SingleImageView(state));
	}

}

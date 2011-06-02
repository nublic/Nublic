/*
 * 
 */
package com.scamall.app.image;


/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class ImageApplication extends NublicApplication {

	@Override
	public void init() {
		ImageWindow mainWindow = new ImageWindow("Nublic_images Application");
		setMainWindow(mainWindow);
//		File imagesFileParent = new File("/var/nublic/Imágenes/Fondos");
		
		SingleImageWindowState state = new SingleImageWindowState();
		state.setListId("/var/nublic/Imágenes/Fondos");
		state.setCurrentPosition(0);
//		BeanItemContainer<Image> images = this.getImagesFromDirectory(imagesFileParent);

		mainWindow.setView(new SingleImageView(state));
		mainWindow.addComponent(mainWindow.getView());
	}

}

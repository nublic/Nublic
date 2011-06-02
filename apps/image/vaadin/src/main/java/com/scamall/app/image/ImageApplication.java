/*
 * 
 */
package com.scamall.app.image;

import java.io.File;
import com.vaadin.data.util.BeanItemContainer;
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
	
	private BeanItemContainer<Image> getImagesFromDirectory(File imagesFileParent) {
		BeanItemContainer<Image> images = new BeanItemContainer<Image>(Image.class); 
		File[] imagesFiles = imagesFileParent.listFiles();
		for (int i = 0; i < imagesFiles.length; i++) {
			Image im = new Image(imagesFiles[i]);
			images.addItem(im); // @TODO image id must be gotten from our node database
		}
		return images;
	}

}

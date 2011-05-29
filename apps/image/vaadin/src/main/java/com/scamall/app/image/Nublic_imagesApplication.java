package com.scamall.app.image;

import java.io.File;
import java.util.ArrayList;

import com.vaadin.Application;
import com.vaadin.ui.*;
/*
 * This class is a hack that allowed me to test the code without the rest of Nublic.
 */
public class Nublic_imagesApplication extends Application {
	@Override
	public void init() {
		Window mainWindow = new Window("Nublic_images Application");
		setMainWindow(mainWindow);
		ArrayList<Image> images = new ArrayList<Image>();
		File imagesFileParent = new File("path_to_file");
		File [] imagesFiles = imagesFileParent.listFiles();
		for(int i=0;i<imagesFiles.length;i++){
			images.add(new Image(imagesFiles[i]));
		}
		
		mainWindow.addComponent(new ImageView(images,0));
	}

}

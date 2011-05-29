/*
 * Copyright 2009 IT Mill Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.scamall.app.image;

import java.io.File;
import java.util.ArrayList;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class ImageApplication extends Application {
	private Window window;

	@Override
	public void init() {
		Window mainWindow = new Window("Nublic_images Application");
		setMainWindow(mainWindow);
		ArrayList<Image> images = new ArrayList<Image>();
		File imagesFileParent = new File("/var/nublic/Imágenes/Fondos");
		File[] imagesFiles = imagesFileParent.listFiles();
		for (int i = 0; i < imagesFiles.length; i++) {
			images.add(new Image(imagesFiles[i]));
		}

		mainWindow.addComponent(new ImageView(images, 0));
	}

}

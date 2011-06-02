/**
 * 
 */
package com.scamall.app.image;

import java.io.File;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;

/** Stores the state of a Image Window.
 * 
 * It is also used to notify from changes in the state to different classes on the controller. 
 * @author David Navarro Estruch
 *
 */
public class SingleImageWindowState implements WindowState {

	/** Search used for the list.
	 *  Initially represents a relative path to the file system
	 */
	private String listId;
	
	/** Container used to list all the images. 
	 * 
	 */
	private BeanItemContainer<Image> listImages;
	
	private int currentPosition;
	
	public String getURL() {
		return "view" + "" + listId + "/" + currentPosition;
	}

	public Image getCurrentImage() {
		return listImages.getIdByIndex(currentPosition);
	}
	
	public BeanItem<Image> getCurrentBeanImage() {
		return listImages.getItem(listImages.getIdByIndex(currentPosition));
	}
	
	public BeanItemContainer<Image> getListImages() {
		return listImages;
	}

	public void setCurrentPosition(int currentPosition) {
		this.currentPosition = currentPosition;
	}
	
	public void setListId(String listId) {
		this.listId = listId;
		this.listImages = getImagesFromDirectory(new File(listId));
	}
	
	public int getCurrentPosition() {
		return currentPosition;
	}
	
	/** Loads the images from a Folder
	 * It does not have control of only load images yet.
	 * 
	 * @param imagesFileParent
	 * @return
	 */
	private BeanItemContainer<Image> getImagesFromDirectory(File imagesFileParent) {
		BeanItemContainer<Image> images = new BeanItemContainer<Image>(Image.class); 
		File[] imagesFiles = imagesFileParent.listFiles();
		for (int i = 0; i < imagesFiles.length; i++) {
			// TODO Check if the mimetype of the file is a valid image
			Image im = new Image(imagesFiles[i]);
			images.addItem(im); 
		}
		return images;
	}
	
}

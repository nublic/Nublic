package com.scamall.app.image;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;

/**
 * Handles the model part of the image, its location in the file system and its
 * thumbnails.
 * 
 * @author Cesar Navarro Estruch
 * 
 */
public class Image implements Serializable {
	private File resource;
	// Add the rest of fields here in the future
	private File resourceThumbnail;
	private File resourceNormalSize;

	private int thumbnailXSize = 120;
	private int thumbnailYSize = 80;

	private int normalXSize = 800;
	private int normalYSize = 500;

	private final static String cacheName = ".cache";
	private long imageId;

	public Image(File resource) {
		this.resource = resource;
	}

	public File getResource() {
		return resource;
	}

	public void setResource(File resource) {
		this.resource = resource;
		this.resourceNormalSize = null;
		this.resourceThumbnail = null;
	}

	public long getImageId() {
		return imageId;
	}

	public void setImageId(long imageId) {
		this.imageId = imageId;
	}

	/**
	 * Returns the normal size file
	 * 
	 * @return Normal size File
	 */
	public File getResourceNormalSize() {
		if (resourceNormalSize == null) {
			String resourceNormalPath = resource.getParent() + "/"
					+ Image.cacheName + "/" + normalXSize + "x" + normalYSize
					+ "/" + resource.getName();
			createIfNotExistsFolder(new File(resourceNormalPath).getParent());
			try {
				resizeImages(resource.getPath(), resourceNormalPath,
						normalXSize, normalYSize);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IM4JavaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.resourceNormalSize = new File(resourceNormalPath);
		}
		return resourceNormalSize;
	}

	/**
	 * Returns the Thumbnail size file
	 * 
	 * @return
	 */
	public File getResourceThumbnail() {
		if (resourceThumbnail == null) {
			String resourceThumbnailPath = resource.getParent() + "/"
					+ Image.cacheName + "/" + thumbnailXSize + "x"
					+ thumbnailYSize + "/" + resource.getName();
			createIfNotExistsFolder(new File(resourceThumbnailPath).getParent());
			try {
				resizeImages(resource.getPath(), resourceThumbnailPath,
						thumbnailXSize, thumbnailYSize);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IM4JavaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.resourceThumbnail = new File(resourceThumbnailPath);
		}

		return resourceThumbnail;
	}

	public void setResourceNormalSize(File resourceNormalSize) {
		this.resourceNormalSize = resourceNormalSize;
	}

	public void setResourceThumbnail(File resourceThumbnail) {
		this.resourceThumbnail = resourceThumbnail;
	}

	private void createIfNotExistsFolder(String folderName) {
		File folder = new File(folderName);
		if (!folder.exists()) {
			folder.mkdirs();
		}
	}

	/**
	 * Utility function to resize an image. It is designed for the thumbnail
	 * versions of the original image.
	 * 
	 * @param sourceImage
	 *            Path to the source image
	 * @param destImage
	 *            Path where the image would be created
	 * @param x
	 *            X coordenate size of the result
	 * @param y
	 *            Y coordenate size of the result
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws IM4JavaException
	 */
	private void resizeImages(String sourceImage, String destImage, int x, int y)
			throws IOException, InterruptedException, IM4JavaException {
		// create command
		ConvertCmd cmd = new ConvertCmd();
		// create the operation, add images and operators/options
		IMOperation op = new IMOperation();
		op.addImage(sourceImage);
		op.resize(x, y);
		op.addImage(destImage);
		// TODO: Add convert to different format when input format is not
		// supported by the browsers
		cmd.run(op);
	}

}

package com.nublic.app.music.client;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.view.client.ProvidesKey;

public class Album {

	  /**
	   * The key provider that provides the unique ID of a contact.
	   */
	  public static final ProvidesKey<Album> KEY_PROVIDER = new ProvidesKey<Album>() {
	    public Object getKey(Album item) {
	      return item == null ? null : item.getAlbumId();
	    }
	  };

	  private static int nextId = 0;

	  private String albumTitle;
	  private final int albumId;
	  private ImageResource albumImage;
	  private String imageHtml;

	  public Album(String alb){
		  this.albumTitle = alb;
		  nextId++;
		  this.albumId  = nextId;
		  this.imageHtml = null;
	  }

	  public int getAlbumId() {
		return albumId;
	  }
	  
	  public ImageResource getAlbumImage() {
		return albumImage;
	  }
	  
	  public String getAlbumTitle() {
		return albumTitle;
	  }
	  
	  public String getImageHtml() {
		return imageHtml;
	  }
	  
	  public void setImageHtml(String imageHtml) {
		this.imageHtml = imageHtml;
	  }
	  
	  public void setAlbumImage(ImageResource albumImage) {
		this.albumImage = albumImage;
	  }
}

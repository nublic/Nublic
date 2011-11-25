package com.nublic.app.music.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.view.client.ProvidesKey;


/**
 * Information about a contact.
 */
public class Artist {
	
//	static interface Images extends ClientBundle {
//	      @Source("defaultContact.jpg")
//	      ImageResource contact();
//	    }

  /**
   * The key provider that provides the unique ID of a contact.
   */
  public static final ProvidesKey<Artist> KEY_PROVIDER = new ProvidesKey<Artist>() {
    public Object getKey(Artist item) {
      return item == null ? null : item.getId();
    }
  };

  private static int nextId = 0;

  private String firstName;
  private final int id;
  private ImageResource imagenArtista;
  private String imageHtml;


  public Artist(String nombre) {
    this.id = nextId;
    nextId++;
    this.firstName = nombre;
    this.imageHtml = null;
  }
  /**
   * @return the unique ID of the contact
   */
  public int getId() {
    return this.id;
  }
  
  public String getFirstName() {
		return firstName;
	}
  
  public ImageResource getImagenArtista() {
	return imagenArtista;
  }
  
  public void setImagenArtista(ImageResource imagenArtista) {
	this.imagenArtista = imagenArtista;
  }
  
  public void setImageHtml(String imageHtml) {
	this.imageHtml = imageHtml;
  }

}
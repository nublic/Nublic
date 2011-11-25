package com.nublic.app.music.client;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AbstractImagePrototype;


class ArtistCell extends AbstractCell<Artist> {

    /**
     * The html of the image used for contacts.
     */
    private final String imageHtml;
    private final ImageResource imagenArtista;
    
    /**
     * The images used for this example.
     */

    static interface Images extends ClientBundle {
      @Source("defaultContact.jpg")
      ImageResource contact();
    }

    /**
     * The Cell used to render a {@link ContactInfo}.
     */

      public ArtistCell(ImageResource image) {
        this.imageHtml = AbstractImagePrototype.create(image).getHTML();
        this.imagenArtista = image;
      }

    @Override
    public void render(Context context, Artist value, SafeHtmlBuilder sb) {
      // Value can be null, so do a null check..
      if (value == null) {
        return;
      }

      value.setImageHtml(imageHtml);
      value.setImagenArtista(imagenArtista);
      ArtistWidget widget = new ArtistWidget(value);
      sb.appendHtmlConstant(widget.getElement().getInnerHTML()); 
   }
 }
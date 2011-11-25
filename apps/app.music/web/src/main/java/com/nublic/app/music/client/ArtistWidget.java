package com.nublic.app.music.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.nublic.app.music.client.AlbumCell.Images;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.user.client.ui.FlowPanel;

public class ArtistWidget extends Composite {

	private static ArtistWidgetUiBinder uiBinder = GWT
			.create(ArtistWidgetUiBinder.class);
	@UiField Image imagen;
	@UiField Label ID;
	@UiField Label nombre;
	@UiField FlowPanel albums;

	interface ArtistWidgetUiBinder extends UiBinder<Widget, ArtistWidget> {
	}

	
	public ArtistWidget(Artist value)  {
		
	
		//TODO arreglar imagenes
		Images images = GWT.create(Images.class);
				
		initWidget(uiBinder.createAndBindUi(this)); 
		
		for (int i=0; i<10; i++ ){
			Album album = new Album(" Genial ");
		  	album.setAlbumImage(images.contact());
		  	album.setImageHtml(AbstractImagePrototype.create(images.contact()).getHTML());
		  	AlbumWidget aw = new AlbumWidget(album);
			albums.add(aw);
		}	  
	    
		nombre.setText(value.getFirstName());
		imagen.setResource(value.getImagenArtista());
		ID.setText("" + value.getId());		
		
	}
}

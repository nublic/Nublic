package com.nublic.app.music.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DragEvent;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.http.client.URL;

public class AlbumWidget extends Composite {

	private static AlbumWidgetUiBinder uiBinder = GWT.create(AlbumWidgetUiBinder.class);
	
	Hyperlink album = new Hyperlink();
	@UiField Label albumTitle;
	@UiField VerticalPanel imageArtistPanel;
	@UiField Image imageAlbum;

	interface AlbumWidgetUiBinder extends UiBinder<Widget, AlbumWidget> {
	}

	public AlbumWidget(Album value) {	
		
		initWidget(uiBinder.createAndBindUi(this));
		
		imageAlbum.setUrl("minipo.jpg");
		imageAlbum.setTitle("Carátula del album");
		
		//TODO hyperlinks detectados, darles forma lógica
		album.getElement().getChild(0).appendChild(imageAlbum.getElement());
		String url = URL.encode("server/thumbnail/" + "estomeloheinventado");
		
		album.setTargetHistoryToken(url);

		albumTitle.setText(value.getAlbumTitle());
		
		imageArtistPanel.add(album); 	
		//albumIm.setResource(value.getAlbumImage());
	}
}

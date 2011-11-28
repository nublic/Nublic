package com.nublic.app.music.client;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dev.shell.Messages;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
//import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.ArtistCell.Images;


public class MusicUi extends Composite {

	private static MusicUiUiBinder uiBinder = GWT.create(MusicUiUiBinder.class);
	@UiField DockLayoutPanel dock;
	//@UiField ArtListWidget widgetList;
	@UiField(provided=true) CellList<Artist> cellList;

	interface MusicUiUiBinder extends UiBinder<Widget, MusicUi> {
	}

	public MusicUi() {
		
		//widgetList = new ArtListWidget();	
		
		Images images = GWT.create(Images.class); 
		ArtistCell artcell = new ArtistCell(images.contact());
		cellList = new CellList<Artist>(artcell);
		
		final NoSelectionModel<Artist> selectionModel = new NoSelectionModel<Artist>();
	    cellList.setSelectionModel(selectionModel);
		// Set the total row count. You might send an RPC request to determine the
	    // total row count.
	    cellList.setRowCount(9, true);	    
	    // Set the range to display. In this case, our visible range is smaller than
	    // the data set.
	    cellList.setVisibleRange(0, 8);
   
	    DataProvider dataProvider = new DataProvider(); 
	    // Connect the list to the data provider.
	    dataProvider.addDataDisplay(cellList);
		initWidget(uiBinder.createAndBindUi(this));
		
	}	
}

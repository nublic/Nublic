package com.nublic.app.music.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.nublic.app.music.client.ArtistCell.Images;

public class ArtListWidget extends Composite {

	private static ArtListWidgetUiBinder uiBinder = GWT
			.create(ArtListWidgetUiBinder.class);
	@UiField(provided=true) CellList<Artist> cellList = null;

	interface ArtListWidgetUiBinder extends UiBinder<Widget, ArtListWidget> {
	}

	public ArtListWidget() {
		Images images = GWT.create(Images.class);  
		 // Create a CellList.
		ArtistCell artcell = new ArtistCell(images.contact());
		cellList = new CellList<Artist>(artcell);
		
		// Set the total row count. You might send an RPC request to determine the
	    // total row count.
	    cellList.setRowCount(4, true);	    
	    // Set the range to display. In this case, our visible range is smaller than
	    // the data set.
	    cellList.setVisibleRange(0, 3);
   
	    //TODO doesnt work
	    final NoSelectionModel<Artist> selectionModel = new NoSelectionModel<Artist>();
	    cellList.setSelectionModel(selectionModel);
	    
	    DataProvider dataProvider = new DataProvider(); 

	    // Connect the list to the data provider.
	    dataProvider.addDataDisplay(cellList);	    
		initWidget(uiBinder.createAndBindUi(this));
	}
}

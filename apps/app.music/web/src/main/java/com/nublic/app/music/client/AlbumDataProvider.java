package com.nublic.app.music.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.nublic.app.music.client.ArtistCell.Images;


	  class AlbumDataProvider extends AsyncDataProvider<Album> {
		  
		// Create a data provider.
		    
		      @Override
		      protected void onRangeChanged(HasData<Album> display) {
		        final com.google.gwt.view.client.Range range = display.getVisibleRange();
				
		        // This timer is here to illustrate the asynchronous nature of this data
		        // provider. In practice, you would use an asynchronous RPC call to
		        // request data in the specified range.
		        new Timer() {
		          @Override
		          public void run() {
		        	Images images = GWT.create(Images.class);	
		            int start = range.getStart();
		            int end = 10;//start + range.getLength();
		            List<Album> dataInRange = new ArrayList<Album>();
				          for (int i = start; i < start + end; i++) {
				        	  Album album = new Album("Genial " + i);
				        	  album.setAlbumImage(images.contact());  
				        	  dataInRange.add(album);
				          }
		            //ART.subList(start, end);
		            // Push the data back into the list.
		            updateRowData(start, dataInRange);
		            //cellList.setRowData(start, dataInRange);
		          }
		        }.schedule(2000);
		      }
	  }

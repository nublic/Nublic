package com.nublic.app.photos.mobile.client.ui;

import java.util.ArrayList;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.gwtmobile.ui.client.event.SelectionChangedEvent;
import com.gwtmobile.ui.client.page.Page;
import com.gwtmobile.ui.client.widgets.ListPanel;
import com.nublic.app.photos.mobile.client.Constants;

public class MainUi extends Page {
	private static MainUiUiBinder uiBinder = GWT.create(MainUiUiBinder.class);
	interface MainUiUiBinder extends UiBinder<Widget, MainUi> {	}

	public MainUi() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField ListPanel list;
	ArrayList<Long> idList = new ArrayList<Long>();
	ArrayList<String> titleList = new ArrayList<String>();
	
	public void setAlbumList(Map<Long, String> albums) {
		list.clear();
		idList.clear();
		
		addNewAlbum(Long.valueOf(-1), Constants.I18N.allPhotos());
				
		for (Map.Entry<Long, String> album : albums.entrySet()) {
			addNewAlbum(album.getKey(), album.getValue());
		}
	}
	
    private void addNewAlbum(Long id, String title) {
    	idList.add(id);
    	titleList.add(title);
    	list.add(new Label(title));
	}

	@UiHandler("list")
	void onListSelectionChanged(SelectionChangedEvent e) {
		goTo(new AlbumGrid(idList.get(e.getSelection()), titleList.get(e.getSelection())));
    }
}

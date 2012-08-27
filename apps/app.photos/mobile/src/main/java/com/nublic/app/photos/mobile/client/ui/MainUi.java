package com.nublic.app.photos.mobile.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.gwtmobile.ui.client.event.SelectionChangedEvent;
import com.gwtmobile.ui.client.page.Page;

public class MainUi extends Page {
	private static MainUiUiBinder uiBinder = GWT.create(MainUiUiBinder.class);
	interface MainUiUiBinder extends UiBinder<Widget, MainUi> {	}

	
	public MainUi() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	
    @UiHandler("list")
	void onListSelectionChanged(SelectionChangedEvent e) {
    	switch (e.getSelection()) {
    	case 0:
    		//goTo(new FilesPage());
    		break;
    	case 1:
    		//goTo(new UploadPhotoPage());
    		break;
    	case 2:
//    		goTo(new CameraUi());
    		break;
    	case 3:
    		//goTo(new PhotosPage(), Transition.SLIDE);
    		break;
    	}
    }


	public Object getNavigationPanel() {
		// TODO Auto-generated method stub
		return null;
	}
}

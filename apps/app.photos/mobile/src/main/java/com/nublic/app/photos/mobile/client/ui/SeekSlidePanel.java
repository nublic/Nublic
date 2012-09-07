package com.nublic.app.photos.mobile.client.ui;

import com.gwtmobile.ui.client.widgets.Slide;
import com.gwtmobile.ui.client.widgets.SlidePanel;

public class SeekSlidePanel extends SlidePanel {

	public void seekToSlide(int index) {
		_current = index;
		Slide slide = getSlide(_current);
		if (slide != null) {
			_panel.clear();
			_panel.add(slide);
		}
	}
	
    @Override
    public void onInitialLoad() {
//    	super.onInitialLoad();
//    	_current = 0;
//    	Slide slide = getSlide(_current);
//    	if (slide != null) {
//    		_panel.add(slide);
//    	}
    }
}

package com.nublic.app.music.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.handlers.TagsChangeHandler;

public class MainUi extends Composite {
	private static MainUiUiBinder uiBinder = GWT.create(MainUiUiBinder.class);
	interface MainUiUiBinder extends UiBinder<Widget, MainUi> {}
	
	DataModel model;

	public MainUi(DataModel model) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.model = model;
		
		addTagsChangeHandler();
	}

	private void addTagsChangeHandler() {
		model.addTagsChangeHandler(new TagsChangeHandler() {
			@Override
			public void onTagsChange() {
								
			}
		});
	}
	
	

}

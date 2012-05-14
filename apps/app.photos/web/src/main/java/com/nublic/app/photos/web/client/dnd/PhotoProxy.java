package com.nublic.app.photos.web.client.dnd;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.photos.web.client.Constants;
import com.nublic.app.photos.web.client.Images;
import com.nublic.app.photos.web.client.model.PhotoInfo;

public class PhotoProxy extends Composite implements DragProxy {
	private static PhotoProxyUiBinder uiBinder = GWT.create(PhotoProxyUiBinder.class);
	interface PhotoProxyUiBinder extends UiBinder<Widget, PhotoProxy> {}
	
	@UiField SimplePanel plusPanel;
	@UiField Label text;
	@UiField Image art;

	public PhotoProxy(PhotoInfo info) {
		initWidget(uiBinder.createAndBindUi(this));
		
		art.setUrl(info.getUrl());
		text.setText(info.getTitle());
	}
	
	public PhotoProxy(int numberOfPhotos) {
		initWidget(uiBinder.createAndBindUi(this));

		art.setResource(Images.INSTANCE.newAlbum());
		text.setText(Constants.I18N.nPhotos(numberOfPhotos));
	}

	@Override
	public void setState(ProxyState state) {
		plusPanel.setVisible(state == ProxyState.PLUS);		
	}

}

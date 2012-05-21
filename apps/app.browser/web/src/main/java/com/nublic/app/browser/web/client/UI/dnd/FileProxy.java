package com.nublic.app.browser.web.client.UI.dnd;

import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.UI.SelectionDetails;
import com.nublic.app.browser.web.client.UI.SelectionInfo;

public class FileProxy extends Composite implements DragProxy {
	private static FileProxyUiBinder uiBinder = GWT.create(FileProxyUiBinder.class);
	interface FileProxyUiBinder extends UiBinder<Widget, FileProxy> { }

	@UiField SimplePanel plusPanel;
	@UiField Label title;
	@UiField Label firstLine;
	@UiField Label secondLine;
	@UiField Image art;
	
	public FileProxy(Set<Widget> selectedFiles) {
		initWidget(uiBinder.createAndBindUi(this));
		
		SelectionInfo info = SelectionDetails.getSelectionInfo(selectedFiles);
		art.setUrl(info.imageURL);
		title.setText(info.title);
		firstLine.setText(info.firstLine);
		secondLine.setText(info.secondLine);
		
		setState(ProxyState.NONE);
	}

	@Override
	public void setState(ProxyState state) {
		plusPanel.setVisible(state == ProxyState.COPY);
	}

}

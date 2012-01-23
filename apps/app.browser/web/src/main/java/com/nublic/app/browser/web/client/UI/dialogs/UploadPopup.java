package com.nublic.app.browser.web.client.UI.dialogs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;

public class UploadPopup extends PopupPanel implements ResizeHandler, ClickHandler {
	UploadContent content = null;

	public UploadPopup(boolean autoHide, boolean modal) {
		super(autoHide, modal);

		Window.addResizeHandler(this);
		content = new UploadContent();
		this.add(content);
		content.addCloseHandler(this);
	}

	@Override
	public void onClick(ClickEvent event) {
		this.hide(true);
	}
	
	public void showDialog(String createInPath) {
		content.setCreationPath(createInPath);
		super.center();
	}

	@Override
	public void onResize(ResizeEvent event) {
		if (isShowing()) {
			super.center();
		}
	}

}
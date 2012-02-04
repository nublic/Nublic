package com.nublic.app.browser.web.client.UI.dialogs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.UI.BrowserUi;

public class NewFolderPopup extends PopupPanel implements ResizeHandler, ClickHandler {
	NewFolderContent content = null;

	public NewFolderPopup(boolean autoHide, boolean modal, BrowserUi feedbackTarget) {
		super(autoHide, modal);

		Window.addResizeHandler(this);
		content = new NewFolderContent(feedbackTarget);
		this.add(content);
		content.addCloseHandler(this);
	}

	@Override
	public void onClick(ClickEvent event) {
		this.hide(true);
	}
	
	public void showDialog(String createInPath) {
		content.setText(Constants.DEFAULT_NEWFOLDER_TEXT);
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

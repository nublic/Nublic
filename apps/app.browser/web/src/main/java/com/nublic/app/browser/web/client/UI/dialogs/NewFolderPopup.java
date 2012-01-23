package com.nublic.app.browser.web.client.UI.dialogs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.PopupPanel;

public class NewFolderPopup extends PopupPanel implements ClickHandler {
	NewFolderContent content = null;
	
	public NewFolderPopup(boolean autoHide, boolean modal) {
		super(autoHide, modal);

		content = new NewFolderContent();
		this.add(content);
		content.addCloseHandler(this);
	}
	
	@Override
	public void onClick(ClickEvent event) {
		this.hide(true);
	}

}

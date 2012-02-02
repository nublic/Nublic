package com.nublic.app.music.client.ui.popup;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.nublic.app.music.client.Constants;

public class ConfirmDeletionPanel extends PopupPanel implements ResizeHandler {
	GeneralPopup content = null;

	public ConfirmDeletionPanel(DeleteHandler delete) {
		super(true, true);

		Window.addResizeHandler(this);
		CancelHandler cancel = new CancelHandler() {
			@Override
			public void onCancel() {
				hide(true);
			}
		};
		
		content = new GeneralPopup(Constants.CONFIRM_DELETION_TITLE, cancel, delete, null);
		content.setWidget(new InfoFill(Constants.CONFIRM_DELETION_INFO));
		this.add(content);
		this.setGlassEnabled(true);
	}


	@Override
	public void onResize(ResizeEvent event) {
		if (isShowing()) {
			super.center();
		}
	}

}

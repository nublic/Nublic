package com.nublic.app.market.web.client.ui;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.nublic.app.market.web.client.Constants;
import com.nublic.app.market.web.client.model.AppStatus;

public class InstallButton extends Button {
	HandlerRegistration overHandlerReg = null;
	HandlerRegistration outHandlerReg = null;

	public void setButtonFromStatus(final AppStatus status) {
		this.addStyleName(status.getCss());
		this.setText(status.getI18NStr());
		this.setEnabled(status.isClickable());
		if (status.isClickable()) {
			overHandlerReg = this.addMouseOverHandler(new MouseOverHandler() {
				@Override
				public void onMouseOver(MouseOverEvent event) {
					InstallButton.this.setText(status.getHoverStr());
					InstallButton.this.removeStyleName(status.getCss());
					InstallButton.this.addStyleName(status.getHoverCss());
				}
			});
			outHandlerReg = this.addMouseOutHandler(new MouseOutHandler() {
				@Override
				public void onMouseOut(MouseOutEvent event) {
					InstallButton.this.setText(status.getI18NStr());
					InstallButton.this.removeStyleName(status.getHoverCss());
					InstallButton.this.addStyleName(status.getCss());
				}
			});
		}
	}
	
	public void removePreviousButtonStatus() {
		this.removeStyleName(Constants.INSTALLED_STYLE);
		this.removeStyleName(Constants.INSTALLED_HOVER_STYLE);
		this.removeStyleName(Constants.INSTALLING_STYLE);
		this.removeStyleName(Constants.NOT_INSTALLED_STYLE);
		this.removeStyleName(Constants.NOT_INSTALLED_HOVER_STYLE);
		this.removeStyleName(Constants.ERROR_STYLE);
		if (overHandlerReg != null) {
			overHandlerReg.removeHandler();
			overHandlerReg = null;
		}
		if (outHandlerReg != null) {
			outHandlerReg.removeHandler();
			outHandlerReg = null;
		}
	}
	
	
}

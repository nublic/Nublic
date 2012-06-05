package com.nublic.app.market.web.client.ui;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Button;
import com.nublic.app.market.web.client.model.AppStatus;

public class Utils {
	public static void setButtonFromStatus(final Button b, final AppStatus status) {
		b.addStyleName(status.getCss());
		b.setText(status.getI18NStr());
		b.setEnabled(status.isClickable());
		if (status.isClickable()) {
			b.addMouseOverHandler(new MouseOverHandler() {
				@Override
				public void onMouseOver(MouseOverEvent event) {
					b.setText(status.getHoverStr());
					b.removeStyleName(status.getCss());
					b.addStyleName(status.getHoverCss());
				}
			});
			b.addMouseOutHandler(new MouseOutHandler() {
				@Override
				public void onMouseOut(MouseOutEvent event) {
					b.setText(status.getI18NStr());
					b.removeStyleName(status.getHoverCss());
					b.addStyleName(status.getCss());
				}
			});
		}
	}
}

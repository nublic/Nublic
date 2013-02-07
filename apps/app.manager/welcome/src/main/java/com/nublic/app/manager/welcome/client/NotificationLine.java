package com.nublic.app.manager.welcome.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class NotificationLine extends Composite implements HasText {
	private static NotificationLineUiBinder uiBinder = GWT.create(NotificationLineUiBinder.class);
	interface NotificationLineUiBinder extends UiBinder<Widget, NotificationLine> { }
	
	@UiField Image icon;
	@UiField Label text;

	public NotificationLine() {
		initWidget(uiBinder.createAndBindUi(this));
		
		// set default notification
		setIcon(Resources.INSTANCE.info());
		setText("Notification");
	}
	
	public void setIcon(ImageResource ir) {
		icon.setResource(ir);
	}

	@Override
	public String getText() {
		return text.getText();
	}

	@Override
	public void setText(String notification) {
		text.setText(notification);		
	}

}

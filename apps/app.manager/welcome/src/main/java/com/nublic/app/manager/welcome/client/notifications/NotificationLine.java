package com.nublic.app.manager.welcome.client.notifications;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.nublic.app.manager.welcome.client.Resources;

public class NotificationLine extends Composite implements HasText {
	private static NotificationLineUiBinder uiBinder = GWT.create(NotificationLineUiBinder.class);
	interface NotificationLineUiBinder extends UiBinder<Widget, NotificationLine> { }
	
	@UiField Image icon;
	@UiField Label text;
	@UiField DeckPanel notiPanel;

	public NotificationLine() {
		initWidget(uiBinder.createAndBindUi(this));
		
		// set default notification
		setIcon(Resources.INSTANCE.info());
		setText("Notification");
		notiPanel.showWidget(0);
	}
	
	public NotificationLine(Notification n) {
		initWidget(uiBinder.createAndBindUi(this));

		// set n notification
		setIcon(n.getIcon());
		setText(n.getText());
		SafeHtml html = n.getHtml();
		if (html != null) {
			notiPanel.add(new HTMLPanel(html));
			notiPanel.showWidget(1);
		} else {
			notiPanel.showWidget(0);
		}
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

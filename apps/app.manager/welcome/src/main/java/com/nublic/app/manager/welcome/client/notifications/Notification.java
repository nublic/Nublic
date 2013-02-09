package com.nublic.app.manager.welcome.client.notifications;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;

public class Notification {
	NotificationType t;
	String text;
	SafeHtml html;
	
	public Notification() {
		this(NotificationType.INFO, "Notification", null);
	}
	
	public Notification(NotificationType t) {
		this(t, "Notification", null);
	}
	
	public Notification(String text) {
		this(NotificationType.INFO, text, null);
	}
	
	public Notification(SafeHtml html) {
		this(NotificationType.INFO, "", html);
	}
	
	public Notification(NotificationType t, String text) {
		this(t, text, null);
	}
	
	public Notification(NotificationType t, SafeHtml html) {
		this(t, "", html);
	}

	public Notification(NotificationType t, String text, SafeHtml html) {
		this.t = t;
		this.text = text;
		this.html = html;
	}

	public NotificationType getType() {
		return t;
	}

	public void setType(NotificationType t) {
		this.t = t;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ImageResource getIcon() {
		return getType().getResource();
	}

	public SafeHtml getHtml() {
		return html;
	}

	public void setHtml(SafeHtml html) {
		this.html = html;
	}
}

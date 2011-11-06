package com.nublic.app.manager.web.client;

import com.google.gwt.dom.client.FrameElement;

public class ExtendedFrameElement extends FrameElement {
	protected ExtendedFrameElement() { }
	
	public final native String getLocationHref() /*-{
		if (this.contentDocument && this.contentDocument.location && this.contentDocument.location.href) {
    		return this.contentDocument.location.href;
		} else {
			return "";
		}
  	}-*/;
	
	public final native String getLocationHash() /*-{
		if (this.contentDocument && this.contentDocument.location && this.contentDocument.location.hash) {
    		return this.contentDocument.location.hash;
		} else {
			return "";
		}
	}-*/;
	
	public final native String getDocumentTitle() /*-{
		if (this.contentDocument && this.contentDocument.title) {
    		return this.contentDocument.title;
		} else {
			return "";
		}
	}-*/;
}

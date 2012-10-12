package com.nublic.app.init.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

public class FooterPagination extends Composite {
	private static FooterPaginationUiBinder uiBinder = GWT.create(FooterPaginationUiBinder.class);
	interface FooterPaginationUiBinder extends UiBinder<Widget, FooterPagination> { }

	interface FooterStyle extends CssResource {
//		String highlighted();
	}
	
	@UiField FooterStyle style;
	@UiField InlineHyperlink previousLink;
	@UiField InlineHyperlink nextLink;
	
	
	public FooterPagination() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void setLinks(String lefturl, String righturl) {
		setPreviousLink(lefturl);
		setNextLink(righturl);
	}
	
	public void setPreviousLink(String url) {
		if (url.isEmpty()) {
			previousLink.setVisible(false);
		} else {
			previousLink.setVisible(true);
			previousLink.setTargetHistoryToken(url);
		}
	}

	public void setNextLink(String url) {
		if (url.isEmpty()) {
			nextLink.setVisible(false);
		} else {
			nextLink.setVisible(true);
			nextLink.setTargetHistoryToken(url);			
		}
	}
	
	public void highlightNext() {
		nextLink.addStyleName("btn-primary");
//		nextLink.removeStyleName("disabled");
//		nextLink.addStyleName(style.highlighted());
	}
	
	public void unhighlightNext() {
		nextLink.removeStyleName("btn-primary");
//		nextLink.addStyleName("disabled");
	}

}

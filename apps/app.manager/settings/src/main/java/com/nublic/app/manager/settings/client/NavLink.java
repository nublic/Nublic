package com.nublic.app.manager.settings.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.InlineHyperlink;

public class NavLink extends InlineHyperlink {

	public void select(boolean selected) {
		Element li = getLiElement();
		if (selected) {
			li.addClassName("active");
		} else {
			li.removeClassName("active");
		}
	}

	private Element getLiElement() {
		return getElement().getParentElement();
	}
	
	public void setTarget(Category c) {
		super.setTargetHistoryToken(Constants.PARAM_CATEGORY + "=" + c.toString());
	}
}

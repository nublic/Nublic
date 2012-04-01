package com.nublic.util.widgets;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.SimplePanel;

public class AnchorPanel extends SimplePanel {
	
    public AnchorPanel() {
        super(DOM.createAnchor());
    }

    public void setHref(String href) {
        getElement().setAttribute("href", href);
    }

    public String getHref() {
        return getElement().getAttribute("href");
    }

    public void setTarget(String frameName) {
        getElement().setAttribute("target", frameName);
    }
}
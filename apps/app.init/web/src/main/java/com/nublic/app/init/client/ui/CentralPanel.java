package com.nublic.app.init.client.ui;

import com.google.gwt.user.client.ui.Composite;
import com.nublic.app.init.client.model.Step;

public abstract class CentralPanel extends Composite {
	public abstract boolean canChangeTo(Step s);
}

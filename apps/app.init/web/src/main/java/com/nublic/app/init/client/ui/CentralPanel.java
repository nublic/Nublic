package com.nublic.app.init.client.ui;

import java.util.EnumSet;

import com.google.gwt.user.client.ui.Composite;
import com.nublic.app.init.client.model.Step;

public abstract class CentralPanel extends Composite {
	public abstract boolean isReady();
	public abstract EnumSet<Step> getNextAllowed();
}

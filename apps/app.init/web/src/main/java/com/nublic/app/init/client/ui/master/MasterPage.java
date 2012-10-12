package com.nublic.app.init.client.ui.master;

import java.util.EnumSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.init.client.Constants;
import com.nublic.app.init.client.model.InitModel;
import com.nublic.app.init.client.model.Step;
import com.nublic.app.init.client.model.handlers.PasswordHandler;
import com.nublic.app.init.client.ui.CentralPanel;
import com.nublic.app.init.client.ui.FooterPagination;
import com.google.gwt.user.client.ui.CheckBox;

public class MasterPage extends CentralPanel {
	private static MasterPageUiBinder uiBinder = GWT.create(MasterPageUiBinder.class);
	interface MasterPageUiBinder extends UiBinder<Widget, MasterPage> { }

	@UiField FooterPagination footer;
	@UiField HTMLPanel passwordPanel;
	@UiField CheckBox confirmCheckbox;

	public MasterPage() {
		initWidget(uiBinder.createAndBindUi(this));
		
		footer.setLinks(Constants.PARAM_PAGE + "=" + Constants.VALUE_USERS,
				Constants.PARAM_PAGE + "=" + Constants.VALUE_NET_CONFIG);
		
		// get password from server
		InitModel.INSTANCE.getMasterPassword(new PasswordHandler() {
			@Override
			public void onPasswordFetch(String password) {
				setPassword(password);
			}
		});
		
		confirmCheckbox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if (event.getValue()) {
					footer.highlightNext();
				} else {
					footer.unhighlightNext();
				}
			}
		});
	}
	
	public void setPassword(String s) {
		passwordPanel.getElement().setInnerText(s);
	}

	@Override
	public boolean canChangeTo(Step s) {
		return EnumSet.of(Step.WELCOME, Step.USERS, Step.MASTER_USER).contains(s) ||
				(s == Step.NET_CONFIG && confirmCheckbox.getValue());
	}

}

package com.nublic.app.browser.web.client.UI.actions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.Resources;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.UI.FileWidget;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;
import com.nublic.util.widgets.PopupButton;
import com.nublic.util.widgets.PopupButtonHandler;
import com.nublic.util.widgets.TextPopup;

public class RenameAction extends ActionWidget {
	
	public RenameAction(BrowserUi stateProvider) {
		super(Resources.INSTANCE.editRename(), "Rename", stateProvider);
	}
	
	private FileWidget getOnlySelected() {
		for (Widget w : stateProvider.getSelectedFiles()) {
			return ((FileWidget)w);
		}
		return null;
	}

	@Override
	public void executeAction() {
		final TextPopup popup = new TextPopup("New name");
		final String showingPath = stateProvider.getShowingPath();
		final FileWidget selected = getOnlySelected();
		final String oldName = selected.getName();
		popup.setText(oldName);
		
		popup.addButtonHandler(PopupButton.OK, new PopupButtonHandler() {
			@Override
			public void onClicked(PopupButton button, ClickEvent event) {
				Message m = new Message() {
					@Override
					public String getURL() {
						return URL.encode(GWT.getHostPageBaseURL() + "server/rename");
					}
					@Override
					public void onSuccess(Response response) {
						if (response.getStatusCode() == Response.SC_OK) {
							// Do nothing
						} else {
							ErrorPopup.showError("Could not rename");
						}
					}
					@Override
					public void onError() {
						ErrorPopup.showError("Could not rename");
					}
				};
				m.addParam("from", showingPath + "/" + oldName);
				m.addParam("to", showingPath + "/" + popup.getText());
				SequenceHelper.sendJustOne(m, RequestBuilder.POST);
				popup.hide();
			}
		});
		
		popup.center();
		popup.selectAndFocus();
	}

	@Override
	public Availability getAvailability() {
		if (stateProvider.getSelectedFiles().size() != 1) {
			return Availability.HIDDEN;
		} else {
			return getOnlySelected().isWritable() ? Availability.AVAILABLE : Availability.UNCLICKABLE;
		}
	}

}

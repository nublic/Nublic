package com.nublic.app.manager.settings.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.nublic.app.manager.settings.client.comm.NublicSyncedFolder;
import com.nublic.util.gwt.LocationUtil;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;

public class WorkFoldersTab extends Composite {

	private static WorkFoldersTabUiBinder uiBinder = GWT.create(WorkFoldersTabUiBinder.class);
	JsArray<NublicSyncedFolder> folders;
	@UiField Button addButton;
	@UiField Button removeButton;
	@UiField ListBox list;
	@UiField Button browseButton;
	@UiField Button changeNameButton;

	interface WorkFoldersTabUiBinder extends UiBinder<Widget, WorkFoldersTab> {
	}

	public WorkFoldersTab() {
		initWidget(uiBinder.createAndBindUi(this));

		SequenceHelper.sendJustOne(new Message() {
			@Override
			public String getURL() {
				return LocationUtil.getHostBaseUrl() + "manager/server/synceds";
			}

			@Override
			public void onSuccess(Response response) {
				if (response.getStatusCode() == Response.SC_OK) {
					folders = JsonUtils.safeEval(response.getText());
					for (int i = 0; i < folders.length(); i++) {
						NublicSyncedFolder folder = folders.get(i);
						list.addItem(folder.getName(), Integer.toString(folder.getId()));
					}
				}
			}

			@Override
			public void onError() {
				// Do nothing
			}
		}, RequestBuilder.GET);
	}

	@UiHandler("browseButton")
	void onBrowseButtonClick(ClickEvent event) {
		if (list.getSelectedIndex() >= 0) {
			String value = list.getValue(list.getSelectedIndex());
			Window.open("/browser/#browser?path=synced/" + value, "_blank", "");
		}
	}
	
	@UiHandler("list")
	void onListDoubleClick(DoubleClickEvent event) {
		onBrowseButtonClick(null);
	}

	@UiHandler("addButton")
	void onAddButtonClick(ClickEvent event) {
	}

	@UiHandler("removeButton")
	void onRemoveButtonClick(ClickEvent event) {
	}

	@UiHandler("changeNameButton")
	void onChangeNameButtonClick(ClickEvent event) {
	}
}

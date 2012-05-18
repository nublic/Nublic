package com.nublic.app.manager.settings.client;

import java.util.EnumSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
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
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.gwt.LocationUtil;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;
import com.nublic.util.widgets.PopupButton;
import com.nublic.util.widgets.PopupButtonHandler;
import com.nublic.util.widgets.TextPopup;

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
			Window.open(Location.getProtocol() + "//" + Location.getHostName() + "/#browser/#browser?path=work-folders/" + value, "_blank", "");
		}
	}
	
	@UiHandler("list")
	void onListDoubleClick(DoubleClickEvent event) {
		onBrowseButtonClick(null);
	}

	@UiHandler("addButton")
	void onAddButtonClick(ClickEvent event) {
		final TextPopup popup = new TextPopup(Constants.I18N.newWorkFolderName(),
				EnumSet.of(PopupButton.ADD, PopupButton.CANCEL), PopupButton.ADD);
		
		popup.addButtonHandler(PopupButton.ADD, new PopupButtonHandler() {
			
			@Override
			public void onClicked(PopupButton button, ClickEvent event) {
				final String name = popup.getText().trim();
				if (name.isEmpty())
					return;
				
				Message m = new Message() {
					@Override
					public String getURL() {
						return LocationUtil.getHostBaseUrl() + "manager/server/synceds";
					}
					@Override
					public void onSuccess(Response response) {
						if (response.getStatusCode() == Response.SC_OK) {
							list.addItem(name, response.getText());
						} else {
							onError();
						}
					}
					@Override
					public void onError() {
						ErrorPopup msg = new ErrorPopup(Constants.I18N.errorNewWorkFolder());
						msg.center();
					}
				};
				m.addParam("name", name);
				SequenceHelper.sendJustOne(m, RequestBuilder.PUT);
				
				popup.hide();
			}
		});
		
		popup.center();
	}

	@UiHandler("removeButton")
	void onRemoveButtonClick(ClickEvent event) {
		if (list.getSelectedIndex() >= 0) {
			final String name = list.getItemText(list.getSelectedIndex());
			final String value = list.getValue(list.getSelectedIndex());
			
			final TextPopup popup = new TextPopup(
					Constants.I18N.confirmRemoval(name),
					EnumSet.of(PopupButton.DELETE, PopupButton.CANCEL),
					PopupButton.DELETE);
			
			popup.addButtonHandler(PopupButton.DELETE, new PopupButtonHandler() {
				
				@Override
				public void onClicked(PopupButton button, ClickEvent event) {
					final String name = popup.getText().trim();
					if (name.isEmpty())
						return;
					
					Message m = new Message() {
						@Override
						public String getURL() {
							return LocationUtil.getHostBaseUrl() + "manager/server/synceds";
						}
						@Override
						public void onSuccess(Response response) {
							if (response.getStatusCode() == Response.SC_OK) {
								list.removeItem(list.getSelectedIndex());
							} else {
								onError();
							}
						}
						@Override
						public void onError() {
							ErrorPopup msg = new ErrorPopup(Constants.I18N.errorRemovalWorkFolder());
							msg.center();
						}
					};
					m.addParam("id", value);
					SequenceHelper.sendJustOne(m, RequestBuilder.DELETE);
					
					popup.hide();
				}
			});
			
			popup.center();
		}
	}

	@UiHandler("changeNameButton")
	void onChangeNameButtonClick(ClickEvent event) {
		if (list.getSelectedIndex() >= 0) {
			final String name = list.getItemText(list.getSelectedIndex());
			final String value = list.getValue(list.getSelectedIndex());
			
			final TextPopup popup = new TextPopup(Constants.I18N.changeWorkFolderName(),
					EnumSet.of(PopupButton.CUSTOM, PopupButton.CANCEL),
					Constants.UTIL_I18N.changeName(), PopupButton.CUSTOM);
			popup.setText(name);
			
			popup.addButtonHandler(PopupButton.CUSTOM, new PopupButtonHandler() {
				
				@Override
				public void onClicked(PopupButton button, ClickEvent event) {
					final String name = popup.getText().trim();
					if (name.isEmpty())
						return;
					
					Message m = new Message() {
						@Override
						public String getURL() {
							return LocationUtil.getHostBaseUrl() + "manager/server/synced-name";
						}
						@Override
						public void onSuccess(Response response) {
							if (response.getStatusCode() == Response.SC_OK) {
								list.setItemText(list.getSelectedIndex(), name);
							} else {
								onError();
							}
						}
						@Override
						public void onError() {
							ErrorPopup msg = new ErrorPopup(Constants.I18N.errorChangeNameWorkFolder());
							msg.center();
						}
					};
					m.addParam("id", value);
					m.addParam("name", name);
					SequenceHelper.sendJustOne(m, RequestBuilder.PUT);
					
					popup.hide();
				}
			});
			
			popup.center();
		}
	}
}

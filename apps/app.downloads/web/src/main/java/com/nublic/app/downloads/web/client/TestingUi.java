package com.nublic.app.downloads.web.client;

import java.io.Serializable;
import java.util.List;

import net.zschech.gwt.comet.client.CometClient;
import net.zschech.gwt.comet.client.CometListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;

public class TestingUi extends Composite implements CometListener {

	private static TestingUiUiBinder uiBinder = GWT.create(TestingUiUiBinder.class);

	interface TestingUiUiBinder extends UiBinder<Widget, TestingUi> {
	}
	@UiField TextBox idText;
	@UiField TextBox sourceText;
	@UiField TextBox targetText;
	@UiField PushButton addButton;
	@UiField PushButton pauseButton;
	@UiField PushButton unpauseButton;
	@UiField PushButton removeButton;
	@UiField PushButton stopButton;
	@UiField TextArea responses;
	
	CometClient client;

	public TestingUi() {
		initWidget(uiBinder.createAndBindUi(this));
		
        client = new CometClient(GWT.getHostPageBaseURL() + "server/poll", this);
        client.start();
	}
	
	void scrollToBottom() {
		responses.getElement().getFirstChildElement().setScrollTop(
				responses.getElement().getFirstChildElement().getScrollHeight());
	}
	
	void addMessage(String msg) {
		responses.setText(responses.getText() + "\n" + msg);
		scrollToBottom();
	}

	@Override
	public void onConnected(int heartbeat) {
		addMessage("Connected");
	}

	@Override
	public void onDisconnected() {
		addMessage("Disconnected");
	}

	@Override
	public void onError(Throwable exception, boolean connected) {
		addMessage("Error -> " + exception.toString());
	}

	@Override
	public void onHeartbeat() {
		addMessage("Heartbeat");
	}

	@Override
	public void onRefresh() {
		addMessage("Refresh");
	}

	@Override
	public void onMessage(List<? extends Serializable> messages) {
		for (Serializable s : messages) {
			addMessage("Message -> " + s.toString());
		}
	}

	@UiHandler("addButton")
	void onAddButtonClick(ClickEvent event) {
		Message m = new Message() {

			@Override
			public String getURL() {
				return GWT.getHostPageBaseURL() + "server/command/add";
			}

			@Override
			public void onSuccess(Response response) {
				Window.alert("add succeeded :)");
			}

			@Override
			public void onError() {
				Window.alert("add error :(");
			}
			
		};
		m.addParam("source", sourceText.getText());
		m.addParam("target", targetText.getText());
		SequenceHelper.sendJustOne(m, RequestBuilder.POST);
	}
	
	@UiHandler("removeButton")
	void onRemoveButtonClick(ClickEvent event) {
		sendIdMessage("remove");
	}
	
	@UiHandler("pauseButton")
	void onPauseButtonClick(ClickEvent event) {
		sendIdMessage("pause");
	}
	
	@UiHandler("unpauseButton")
	void onUnpauseButtonClick(ClickEvent event) {
		sendIdMessage("unpause");
	}
	
	@UiHandler("stopButton")
	void onStopButtonClick(ClickEvent event) {
		sendIdMessage("stop");
	}
	
	void sendIdMessage(final String url) {
		Message m = new Message() {

			@Override
			public String getURL() {
				return GWT.getHostPageBaseURL() + "server/command/" + url;
			}

			@Override
			public void onSuccess(Response response) {
				Window.alert(url + " succeeded :)");
			}

			@Override
			public void onError() {
				Window.alert(url + " error :(");
			}
			
		};
		m.addParam("id", idText.getText());
		SequenceHelper.sendJustOne(m, RequestBuilder.POST);
	}
}

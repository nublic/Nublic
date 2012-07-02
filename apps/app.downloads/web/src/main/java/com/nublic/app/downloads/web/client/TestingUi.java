package com.nublic.app.downloads.web.client;

import java.io.Serializable;
import java.util.List;

import net.zschech.gwt.comet.client.CometClient;
import net.zschech.gwt.comet.client.CometListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class TestingUi extends Composite implements CometListener {

	private static TestingUiUiBinder uiBinder = GWT.create(TestingUiUiBinder.class);

	interface TestingUiUiBinder extends UiBinder<Widget, TestingUi> {
	}
	
	@UiField TextBox request;
	@UiField TextArea responses;
	
	CometClient client;

	public TestingUi() {
		initWidget(uiBinder.createAndBindUi(this));
		
        client = new CometClient(GWT.getHostPageBaseURL() + "server/poll", this);
        client.start();
        
        request.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					event.preventDefault();
					// TODO: Send event
					request.setText("");
				}
			}
		});
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

}

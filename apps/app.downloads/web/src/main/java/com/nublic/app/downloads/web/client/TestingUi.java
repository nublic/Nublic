package com.nublic.app.downloads.web.client;

import java.io.Serializable;
import java.util.List;

import org.atmosphere.gwt.client.AtmosphereClient;
import org.atmosphere.gwt.client.AtmosphereListener;

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

public class TestingUi extends Composite implements AtmosphereListener {

	private static TestingUiUiBinder uiBinder = GWT.create(TestingUiUiBinder.class);

	interface TestingUiUiBinder extends UiBinder<Widget, TestingUi> {
	}
	
	@UiField TextBox request;
	@UiField TextArea responses;
	
	AtmosphereClient client;

	public TestingUi() {
		initWidget(uiBinder.createAndBindUi(this));
		
        client = new AtmosphereClient(GWT.getModuleBaseURL() + "server", this);
        client.start();
        
        request.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					event.preventDefault();
					client.post(request.getText());
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
	public void onConnected(int heartbeat, int connectionID) {
		addMessage("Connected with id " + connectionID);
	}

	@Override
	public void onBeforeDisconnected() {
		addMessage("Before disconnected");
	}

	@Override
	public void onDisconnected() {
		addMessage("Disconnected");
	}

	@Override
	public void onError(Throwable exception, boolean connected) {
		addMessage("Error -> " + exception.getMessage());
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
	public void onAfterRefresh() {
		addMessage("After resfresh");
	}

	@Override
	public void onMessage(List<? extends Serializable> messages) {
		for (Object m : messages) {
			addMessage("Message -> " + m.toString());
		}
	}

}

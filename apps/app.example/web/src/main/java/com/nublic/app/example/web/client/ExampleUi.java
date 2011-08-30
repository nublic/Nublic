package com.nublic.app.example.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Label;

public class ExampleUi extends Composite {

	private static ExampleUiUiBinder uiBinder = GWT.create(ExampleUiUiBinder.class);
	@UiField
	Button greetButton;
	@UiField
	TextBox nameText;
	@UiField
	Label greetLabel;
	
	private static final String ERROR_MSG = "There was an error contacting the server.";

	// In a real server
	// private static final String SERVER_URL = GWT.getModuleBaseURL() +
	// "server/say/";
	// For testing purposes
	private static final String SERVER_URL = "http://localhost:8080/example/server/say/";

	interface ExampleUiUiBinder extends UiBinder<Widget, ExampleUi> {
	}

	public ExampleUi() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("greetButton")
	void onGreetButtonClick(ClickEvent event) {
		String name = nameText.getText();
		if (name.length() > 0) {
			History.newItem(name, false);
			handleUserRequest(name);
		}
	}
	
	public void showName(String name) {
		nameText.setText(name);
		handleUserRequest(name);
	}

	void updateGreetingLabel(String newLabel) {
		greetLabel.setText(newLabel);
	}

	void handleUserRequest(String name) {
		String url = URL.encode(SERVER_URL + name);
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);

		try {
			// Read http://stackoverflow.com/questions/5518485/getting-and-using-remote-json-data
			@SuppressWarnings("unused")
			// It is not unused, we maintain callbacks
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					new ErrorPopup(ERROR_MSG).show();
				}

				public void onResponseReceived(Request request, Response response) {
					if (Response.SC_OK == response.getStatusCode()) {
						// Update elements
						String text = response.getText();
						Greeting g = JsonUtils.safeEval(text);
						updateGreetingLabel(g.getGreeting());
					} else {
						new ErrorPopup(ERROR_MSG).show();
					}
				}
			});
		} catch (RequestException e) {
			new ErrorPopup(ERROR_MSG).show();
		}
	}
}

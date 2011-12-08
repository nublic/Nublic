package com.nublic.util.messages;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PostRedirectHelper {
	HashMap<String, String> params = new HashMap<String, String>();
	String url = "";

	public PostRedirectHelper() {
	}

	public PostRedirectHelper(String url) {
		this.url = url;
	}

	public PostRedirectHelper(String url, HashMap<String, String> params) {
		this.url = url;
		this.params = params;
	}
	
	public void setURL(String url) {
		this.url = url;
	}

	public String getURL() {
		return url;
	}
	
	public void addParam(String key, String value) {
		params.put(key, value);
	}

	public HashMap<String, String> getParams() {
		return params;
	}

	public void send() {
		final FormPanel form = new FormPanel();
		VerticalPanel formContent = new VerticalPanel();
		form.add(formContent);

		form.setAction(GWT.getHostPageBaseURL() + "server/zip-set");
		form.setEncoding(FormPanel.ENCODING_URLENCODED);
		form.setMethod(FormPanel.METHOD_POST);

		// Set params
		for (String key : params.keySet()) {
			TextBox param = new TextBox();
			param.setName(key);
			param.setValue(params.get(key));
			formContent.add(param);
		}

		form.setVisible(false);
		RootPanel.get().add(form);

		// Make the request
		form.submit();
		
		// Remove the hack-panel from the root
		form.addSubmitHandler(new SubmitHandler() {
			@Override
			public void onSubmit(SubmitEvent event) {
				RootPanel.get().remove(form);
			}
		});
	}
}

package com.nublic.util.messages;

import java.util.HashMap;

import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PostRedirectHelper {
	HashMap<String, String> params = new HashMap<String, String>();
	String url = "";
	FileUpload fileUpload = null;
	String paramFileUploadName = "";

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
	
	public void addParam(String key, FileUpload value) {
		fileUpload = value;
		paramFileUploadName = key;
	}

	public HashMap<String, String> getParams() {
		return params;
	}
	
	public void send() {
		send(null);
	}

	public void send(SubmitCompleteHandler h) {
		final FormPanel form = new FormPanel();
		VerticalPanel formContent = new VerticalPanel();
		form.add(formContent);

		form.setAction(url);
		form.setMethod(FormPanel.METHOD_POST);
		if (fileUpload == null) {
			form.setEncoding(FormPanel.ENCODING_URLENCODED);
		} else {
			form.setEncoding(FormPanel.ENCODING_MULTIPART);
			fileUpload.setName(paramFileUploadName);
			formContent.add(fileUpload);
		}

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
		if (h != null) {
			form.addSubmitCompleteHandler(h);
		}
	}
}

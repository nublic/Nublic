package com.nublic.util.users;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.util.messages.Message;

public class UserListMessage extends Message {
	UserListHandler ulh;
	
	public UserListMessage(UserListHandler ulh) {
		this.ulh = ulh;
	}
	
	@Override
	public String getURL() {
		return URL.encode("/manager/server/userlist/");
	}

	@Override
	public void onSuccess(Response response) {
		if (response.getStatusCode() == Response.SC_OK) {
			String text = response.getText();

			JsArray <JSUser> jsUserList = null;
			jsUserList = JsonUtils.safeEval(text);
			
			if (jsUserList == null) {
				onError();
			} else {
				List<User> userList = new ArrayList<User>();
				for (int i = 0 ; i < jsUserList.length() ; i++) {
					User u = new User(jsUserList.get(i));
					userList.add(u);
				}
				ulh.onUserList(userList);
			}

		} else {
			onError();
		}
	}

	@Override
	public void onError() {
//		ErrorPopup.showError(Constants.I18N.couldNotCreateUser());
	}

}

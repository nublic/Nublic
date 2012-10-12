package com.nublic.app.init.client.model.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.init.client.model.handlers.AddUserHandler;
import com.nublic.util.messages.Message;

public class AddUserMessage extends Message {
	String name;
	String password;
	AddUserHandler auh;
	
	public AddUserMessage(String name, String password, AddUserHandler auh) {
		this.name = name;
		this.password = password;
		this.auh = auh;
	}
	
	@Override
	public String getURL() {
		addParam("password", password);
		addParam("name", name);
		return URL.encode(GWT.getHostPageBaseURL() + "server/adduser/");
	}

	@Override
	public void onSuccess(Response response) {
		if (response.getStatusCode() == Response.SC_OK) {
//			String text = response.getText();
//
//			List<Playlist> involvedSet = new ArrayList<Playlist>();
//			Playlist p = new Playlist(text, name);
//			involvedSet.add(p);
//			PlaylistsChangeEvent event = new PlaylistsChangeEvent(PlaylistsChangeEventType.PLAYLISTS_ADDED, involvedSet);
//			Controller.INSTANCE.getModel().firePlaylistsHandlers(event);
//			Controller.INSTANCE.getModel().getPlaylistCache().put(text, p);
			auh.onUserAdded(name);
		} else {
			onError();
		}
	}

	@Override
	public void onError() {
//		ErrorPopup.showError(Constants.I18N.addPlaylistError());
		auh.onUserAdded(name);
	}

}

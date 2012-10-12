package com.nublic.app.init.client.model.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.init.client.model.handlers.CheckNublicNameHandler;
import com.nublic.util.messages.Message;

public class CheckNublicNameMessage extends Message {
	String name;
	CheckNublicNameHandler cnnh;
	
	public CheckNublicNameMessage(String name, CheckNublicNameHandler cnnh) {
		this.name = name;
		this.cnnh = cnnh;
	}
	
	@Override
	public String getURL() {
		//addParam("name", name);
		return URL.encode(GWT.getHostPageBaseURL() + "server/checknublicname/" + name);
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
			cnnh.onNublicNameChecked(name, true);
		} else {
			onError();
		}
	}

	@Override
	public void onError() {
//		ErrorPopup.showError(Constants.I18N.addPlaylistError());
		if (name.length() < 3) {
			cnnh.onNublicNameChecked(name, false);
		} else {
			cnnh.onNublicNameChecked(name, true);
		}
	}

}

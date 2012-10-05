package com.nublic.app.init.client.model.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.init.client.model.handlers.PasswordHandler;
import com.nublic.util.messages.Message;

public class PasswordMessage extends Message {
	PasswordHandler ph;
	
	public PasswordMessage(PasswordHandler ph) {
		this.ph = ph;
	}
	
	@Override
	public String getURL() {
		return URL.encode(GWT.getHostPageBaseURL() + "server/password/");
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
			ph.onPasswordFetch("ThisIsAPasswordExample0123456789");
		} else {
			onError();
		}
	}

	@Override
	public void onError() {
//		ErrorPopup.showError(Constants.I18N.addPlaylistError());
		ph.onPasswordFetch("ThisIsAPasswordExample0123456789");
	}

}

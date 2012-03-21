package com.nublic.app.music.client.datamodel.messages;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.app.music.client.datamodel.handlers.SongHandler;
import com.nublic.app.music.client.datamodel.js.JSSong;
import com.nublic.app.music.client.datamodel.js.JSSongResponse;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.Message;

//GET /playlist/:id/:order/:asc-desc/:start/:length
//* Gets all the songs from the playlist
//* Return: as in /songs
public class PlaylistContentMessage extends Message {
	int from;
	int to;
	String id;
	SongHandler ph;
	int targetScreen;
	DataModel model;

	public PlaylistContentMessage(int from, int to, String id, SongHandler ph, int currentScreen, DataModel model) {
		this.from = from;
		this.to = to;
		this.id = id;
		this.ph = ph;
		this.targetScreen = currentScreen;
		this.model = model;
	}

	@Override
	public String getURL() {
		StringBuilder url = new StringBuilder();
		url.append(GWT.getHostPageBaseURL());
		url.append("server/playlist/");
		url.append(id);
		url.append("/playlist/asc/");
		url.append(from);
		url.append("/");
		url.append(to - from + 1);
		return URL.encode(url.toString());
	}

	@Override
	public void onSuccess(Response response) {
		if (response.getStatusCode() == Response.SC_OK) {
			// Commented for response including row count.
			String text = response.getText();
			JSSongResponse jsResponse = JsonUtils.safeEval(text);			
			if (jsResponse == null) {
				onError();
			} else {
				List<SongInfo> answerList = new ArrayList<SongInfo>();
				JsArray<JSSong> songList = jsResponse.getSongs();
				for (int i = 0; i < songList.length(); i++) {
					JSSong song = songList.get(i);
					SongInfo info = new SongInfo(song.getId(),
										 song.getTitle(),
										 song.getArtistId(),
										 song.getAlbumId(),
										 song.getTrack(),
										 song.getLength());
					answerList.add(info);
				}
				// Only if the message arrives on time to fill the screen it was meant for
				if (targetScreen == model.getCurrentScreen()) {
					ph.onSongsChange(jsResponse.getRowCount(), from, to, answerList);
				}
			}
		} else {
			onError();
		}
	}

	@Override
	public void onError() {
		ErrorPopup.showError("Could not get songs in playlist");
	}

}

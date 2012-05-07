package com.nublic.app.music.client.ui;

public enum ButtonType {
	DELETE_COLLECTION(ButtonLineParam.DELETE),
	DELETE_PLAYLIST(ButtonLineParam.DELETE),
	DELETE_PLAYLIST_SONG(ButtonLineParam.DELETE),
	DELETE_COLLECTION_SONG(ButtonLineParam.DELETE),
	PLAY_ARTIST(ButtonLineParam.PLAY),
	PLAY_ALBUM(ButtonLineParam.PLAY),
	PLAY_SONG(ButtonLineParam.PLAY),
	PLAY_PLAYLIST(ButtonLineParam.PLAY),
	PLAY_COLLECTION(ButtonLineParam.PLAY),
	EDIT_ARTIST(ButtonLineParam.EDIT),
	EDIT_ALBUM(ButtonLineParam.EDIT),
	EDIT_SONG(ButtonLineParam.EDIT);

	ButtonLineParam p;
	
	private ButtonType(ButtonLineParam p) {
		this.p = p;
	}
	
	public ButtonLineParam getParam() {
		return p;
	}
	
}

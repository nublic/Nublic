package com.nublic.app.music.client.ui.playlist;

import java.util.EnumSet;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.datamodel.Controller;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.app.music.client.datamodel.handlers.DeleteButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.PlayButtonHandler;
import com.nublic.app.music.client.ui.ButtonLine;
import com.nublic.app.music.client.ui.ButtonLineParam;
import com.nublic.app.music.client.ui.song.PlaylistSongList;
import com.nublic.app.music.client.ui.song.SongList;

public class PlaylistPanel extends Composite {
	private static PlaylistPanelUiBinder uiBinder = GWT.create(PlaylistPanelUiBinder.class);
	interface PlaylistPanelUiBinder extends UiBinder<Widget, PlaylistPanel> { }

	@UiField FlowPanel mainPanel;
	@UiField Label titleLabel;
	@UiField HorizontalPanel titlePanel;
	
	public PlaylistPanel(String id) {
		initWidget(uiBinder.createAndBindUi(this));
				
		titleLabel.setText(Controller.getModel().getPlaylistCache().get(id).getName());

		// Create button line
		EnumSet<ButtonLineParam> buttonSet = EnumSet.of(ButtonLineParam.PLAY);
		if (!id.equals(Constants.CURRENT_PLAYLIST_ID)) {
			buttonSet.add(ButtonLineParam.DELETE);
		}
		ButtonLine b = new ButtonLine(buttonSet);
		setDeleteButtonHandler(b);
		setPlayButtonHandler(b);
		titlePanel.add(b);
	}
	
	
	public void setSongList(int total, int from, int to, List<SongInfo> answerList, String playlistId) {
		SongList sl = new PlaylistSongList(playlistId, total, mainPanel);
		sl.addSongs(total, from, to, answerList);

		mainPanel.add(sl);
	}
	
	private void setDeleteButtonHandler(ButtonLine b) {
		b.setDeleteButtonHandler(new DeleteButtonHandler() {
			@Override
			public void onDelete() {
				// TODO delete
			}
		});
	}

	private void setPlayButtonHandler(ButtonLine b) {
		b.setPlayButtonHandler(new PlayButtonHandler() {
			@Override
			public void onPlay() {
				// TODO: play
			}
		});
	}

}

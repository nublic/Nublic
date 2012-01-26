package com.nublic.app.music.client.ui;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.Playlist;
import com.nublic.app.music.client.datamodel.Tag;
import com.nublic.app.music.client.datamodel.handlers.PlaylistsChangeHandler;
import com.nublic.app.music.client.datamodel.handlers.TagsChangeHandler;

public class MainUi extends Composite {
	private static MainUiUiBinder uiBinder = GWT.create(MainUiUiBinder.class);
	interface MainUiUiBinder extends UiBinder<Widget, MainUi> {}
	
	@UiField VerticalPanel tagsPanel;
	@UiField VerticalPanel playlistsPanel;
	DataModel model;

	public MainUi(DataModel model) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.model = model;
		
		addTagsChangeHandler();
		addPlaylistsChangeHandler();
	}
	
	// Handler to handle changes in playlists list
	private void addPlaylistsChangeHandler() {
		model.addPlaylistsChangeHandler(new PlaylistsChangeHandler() {
			@Override
			public void onPlaylistsChange() {
				// Clear panel
				playlistsPanel.clear();
				// Add current playlist, which is always there
				PlaylistWidget currentPlaylist = new PlaylistWidget("Current playlist");
				currentPlaylist.setSelected(true);
				playlistsPanel.add(currentPlaylist);
				// Add rest of playlists
				List<Playlist> playlistList = model.getPlaylistList();
				for (Playlist p : playlistList) {
					playlistsPanel.add(new PlaylistWidget(p.getName()));
				}
			}
		});
	}

	// Handler to handle changes in collections list
	private void addTagsChangeHandler() {
		model.addTagsChangeHandler(new TagsChangeHandler() {
			@Override
			public void onTagsChange() {
				tagsPanel.clear();
				List<Tag> tagList = model.getTagList();
				for (Tag t : tagList) {
					tagsPanel.add(new PlaylistWidget(t.getName()));
				}
			}
		});
	}

}

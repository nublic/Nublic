package com.nublic.app.music.client.ui.artist;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.nublic.app.music.client.datamodel.Artist;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.handlers.AddAtEndButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.DeleteButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.PlayButtonHandler;
import com.nublic.app.music.client.ui.ButtonLine;
import com.nublic.app.music.client.ui.popup.ConfirmDeletionPanel;
import com.nublic.app.music.client.ui.popup.DeleteHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class ArtistPanel extends Composite {
	private static ArtistPanelUiBinder uiBinder = GWT.create(ArtistPanelUiBinder.class);
	interface ArtistPanelUiBinder extends UiBinder<Widget, ArtistPanel> { }
	
	@UiField FlowPanel mainPanel;
	@UiField Label titleLabel;
	@UiField HorizontalPanel titlePanel;

	DataModel model;
	String collectionId;
	String collectionName;
	List<Artist> artistList;
	ConfirmDeletionPanel cdp = new ConfirmDeletionPanel(new DeleteHandler() {
		@Override
		public void onDelete() {
			model.deleteTag(collectionId);
			cdp.hide();
		}
	});

	public ArtistPanel(DataModel model, String collectionId, String collectionName) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.model = model;
		this.collectionId = collectionId;
		this.collectionName = collectionName;
		
		titleLabel.setText(collectionName);
		
		// Create button line
		boolean canBeDeleted = collectionId == null ? false : true;
		ButtonLine b = new ButtonLine(canBeDeleted, false, true, true, titlePanel);
		setDeleteButtonHandler(b);
		setAddAtEndButtonHandler(b);
		setPlayButtonHandler(b);
		titlePanel.add(b);
	}

	public void setArtistList(List<Artist> artistList) {
		this.artistList = artistList;

		for (Artist a : artistList) {
			ArtistWidget aw = new ArtistWidget(model, a);
			mainPanel.add(aw);
		}
	}
	
	// Handlers for button line
	private void setDeleteButtonHandler(ButtonLine b) {
		b.setDeleteButtonHandler(new DeleteButtonHandler() {
			@Override
			public void onDelete() {
				cdp.center();
			}
		});
	}

	private void setAddAtEndButtonHandler(ButtonLine b) {
		b.setAddAtEndButtonHandler(new AddAtEndButtonHandler() {
			@Override
			public void onAddAtEnd() {
				// TODO: addAtEnd
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

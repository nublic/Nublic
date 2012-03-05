package com.nublic.app.music.client.ui.song;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.datamodel.AlbumInfo;
import com.nublic.app.music.client.datamodel.ArtistInfo;
import com.nublic.app.music.client.datamodel.Controller;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.app.music.client.datamodel.handlers.AddAtEndButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.SongHandler;
import com.nublic.app.music.client.ui.ButtonLine;
import com.nublic.app.music.client.ui.ButtonLineParam;
import com.nublic.util.cache.CacheHandler;
import com.nublic.util.messages.DefaultComparator;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceIgnorer;
import com.nublic.util.range.Range;

public class SongList extends Composite implements ScrollHandler {
	private static SongListUiBinder uiBinder = GWT.create(SongListUiBinder.class);
	interface SongListUiBinder extends UiBinder<Widget, SongList> { }
	
	// CSS Styles defined in the .xml file
	interface SongStyle extends CssResource {
		String alignright();
		String leftmargin();
		String rightmargin();
		String bottommargin();
	}

	@UiField SongStyle style;
	@UiField Grid grid;
	DataModel model;
	Widget scrollPanel;
	String albumId;
	String artistId;
	String collectionId;
	String playlistId = null;
	int numberOfSongs;
	SongListType songListType;
	
	
	SequenceIgnorer<Message> sendHelper = new SequenceIgnorer<Message>(DefaultComparator.INSTANCE);
	List<SongLocalizer> unloadedLocalizers;
	HashMap<Integer, SongLocalizer> localizerIndex;
	List<Range> askedRanges = new ArrayList<Range>();
	MySongHandler songHandler;

	
	public SongList(DataModel model, String playlistId, int numberOfSongs, Widget scrollPanel) {
		this(model, null, null, null, numberOfSongs, scrollPanel, SongListType.SONG_IN_PLAYLIST);
		this.playlistId = playlistId;
	}
	
	public SongList(DataModel model, String albumId, String artistId, String collectionId, int numberOfSongs, Widget scrollPanel) {
		this(model, albumId, artistId, collectionId, numberOfSongs, scrollPanel, SongListType.SONG_IN_ALBUM);
	}
	
	// With numberOfSongs == -1 we'll get it from first request
	// Scroll panel which we in are in to handle lazy loading
	public SongList(DataModel model, String albumId, String artistId, String collectionId, int numberOfSongs, Widget scrollPanel, SongListType type) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.model = model;
		this.albumId = albumId;
		this.artistId = artistId;
		this.collectionId = collectionId;
		this.numberOfSongs = numberOfSongs;
		this.scrollPanel = scrollPanel;
		this.songListType = type;

		songHandler = new MySongHandler();
		
		if (numberOfSongs == -1) {
			
		} else {
			prepareGrid();
			prepareLocalizers(numberOfSongs);
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					// This is executed when current events stack empties
					// Call onScroll when the widgets are loaded, so content of shown ones can be lazy loaded
					onScroll(null);
				}
			});
		}
	}
	
	
	// +++ Things related to lazy loading +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private class MySongHandler implements SongHandler {
		@Override
		public void onSongsChange(int total, int from, int to, List<SongInfo> answerList) {
			if (total != SongList.this.numberOfSongs) {
				// TODO: rescale songlist
			} else {
				int currentSong = from;
				for (SongInfo s : answerList) {
					setSong(currentSong, s);
					// Maybe it's more efficient to remove all localizers applying a filter from <= .getPosition <= to
					SongLocalizer loc = localizerIndex.get(currentSong);
					unloadedLocalizers.remove(loc);
					currentSong++;
				}
			}
		}
	}
	
	public void addSongs(int total, int from, int to, List<SongInfo> songList) {
		songHandler.onSongsChange(total, from, to, songList);
	}

	private void prepareLocalizers(int amount) {
		localizerIndex = new HashMap<Integer, SongLocalizer>(amount);
		unloadedLocalizers = new ArrayList<SongLocalizer>(amount);
		for (int i = 0; i < amount; i++) {
			SongLocalizer loc = new SongLocalizer(i);
			unloadedLocalizers.add(loc);
			localizerIndex.put(i, loc);
			grid.setWidget(i, 0, loc);
		}
		scrollPanel.addDomHandler(this, ScrollEvent.getType());
	}

	@Override
	public void onScroll(ScrollEvent event) {
		int panelTop = scrollPanel.getAbsoluteTop();
		int panelBottom = panelTop + scrollPanel.getOffsetHeight();

		boolean needToLoad = false;
		List<Range> rangeToAsk = new ArrayList<Range>();
		for (int i = 0; i < unloadedLocalizers.size() && !needToLoad; i++) {
			// We try to find first unloadedWidgets which need to be lazy-loaded
			SongLocalizer sl = unloadedLocalizers.get(i);
//			if (sl.isInRange(panelTop, panelBottom)) {
			if (!Range.contains(askedRanges, sl.getPosition()) && sl.isNearRange(panelTop, panelBottom)) {
				// If we find it we'll construct a request asking for the previous 10 to it and next 30 (if are unloaded and not waiting for answer)
				needToLoad = true;
				rangeToAsk.add(findRangeFromPosition(sl.getPosition()));
				trimRangeToAsk(rangeToAsk);
			}
		}
		if (needToLoad) {
			for (Range r : rangeToAsk) {
				askedRanges.add(r);
				if (playlistId == null) {
					model.askForSongs(r.getFrom(), r.getTo(), albumId, artistId, collectionId, songHandler);
				} else {
					model.askForPlaylistSongs(r.getFrom(), r.getTo(), playlistId, songHandler);
				}
			}
		}
	}
	
	private Range findRangeFromPosition(int position) {
		int unboundedFrom = position - Constants.PREVIOUS_SONGS_TO_ASK;
		int from = unboundedFrom <= 0 ? 0 : unboundedFrom;
		int unboundedTo = position + Constants.NEXT_SONGS_TO_ASK;
		int to = unboundedTo >= numberOfSongs ? numberOfSongs -1 : unboundedTo;
		return new Range(from, to);
	}
	
	private void trimRangeToAsk(List<Range> rangeToAsk) {
		for (Range r : askedRanges) {
			Range.remove(rangeToAsk, r);
		}
	}

	// TODO: make a function to invalid askedRange if request fails
	
	// +++ Things related to ui fill +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public void setSong(int row, SongInfo s) {
		grid.getRowFormatter().getElement(row).addClassName("translucidPanel");
		switch (songListType) {
		case SONG_IN_PLAYLIST:
			setPlaylistSong(row, s);
			break;
		case SONG_IN_ALBUM:
			setAlbumSong(row, s);
			break;
		}
	}
	
	private void prepareGrid() {
		switch (songListType) {
		case SONG_IN_PLAYLIST:
			grid.resize(numberOfSongs, 6);
			break;
		case SONG_IN_ALBUM:
			grid.resize(numberOfSongs, 2);
			grid.getColumnFormatter().setWidth(0, Constants.FIRST_COLUMN_WIDTH);
			break;
		}
	}
	
	private void setAlbumSong(int row, SongInfo s) {
		setTrackNumber(row, 0, s.getTrack()); 		// Column 0
		setTitleLenght(row, 1, s);					// Column 1
	}

	private void setPlaylistSong(int row, SongInfo s) {
		setButtons(row, 0, s);						// Column 0
		setTrackNumber(row, 1, s.getTrack());		// Column 1
		setTitle(row, 2, s.getTitle());				// Column 2
		setLenght(row, 3, s.getFormattedLength());	// Column 3
		setAlbum(row, 4, s);						// Column 4
		setArtist(row, 5, s);						// Column 5
	}

	// +++ Methods to fill the grid +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private void setLenght(int row, int column, String formattedLength) {
		Label titleLabel = new Label(formattedLength);
		titleLabel.getElement().addClassName(style.leftmargin());
		grid.setWidget(row, column, titleLabel);
	}

	private void setTitle(int row, int column, String title) {
		Label titleLabel = new Label(title);
		titleLabel.getElement().addClassName(style.leftmargin());
		grid.setWidget(row, column, titleLabel);
	}

	private void setButtons(int row, int column, SongInfo s) {
		ButtonLine buttonLine = new ButtonLine(EnumSet.of(ButtonLineParam.PLAY, ButtonLineParam.DELETE));
		buttonLine.getElement().addClassName(style.rightmargin());
		grid.setWidget(row, column, buttonLine);
	}

	private void setTrackNumber(int row, int column, int track) {
		String trackStr = track == -1 ? "-" : String.valueOf(track);
		Label trackNumLabel = new Label(trackStr);
		trackNumLabel.getElement().addClassName(style.alignright());
		HorizontalPanel capsule = new HorizontalPanel();
		capsule.setWidth("100%");
		capsule.setHeight("25px");
		capsule.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		capsule.add(trackNumLabel);
		grid.setWidget(row, column, capsule);
	}
	
	private void setTitleLenght(int row, int column, SongInfo s) {
		Label titleLabel = new Label(s.getTitle() + " (" +  s.getFormattedLength() + ")");
		ButtonLine buttonLine = new ButtonLine(EnumSet.of(ButtonLineParam.PLAY, ButtonLineParam.ADD_AT_END, ButtonLineParam.EDIT));
		buttonLine.setAddAtEndButtonHandler(new MyAddAtEndHandler(s));
		HorizontalPanel h = new HorizontalPanel();
		h.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		h.add(titleLabel);
		h.add(buttonLine);
		h.getElement().addClassName(style.leftmargin());
		grid.setWidget(row, column, h);
	}

	private void setAlbum(int row, int column, SongInfo s) {
		final Label albumLabel = new Label();
		albumLabel.getElement().addClassName(style.leftmargin());
		Controller.getAlbumCache().addHandler(s.getAlbumId(), new CacheHandler<String, AlbumInfo>() {
//		model.getAlbumCache().addHandler(s.getAlbumId(), new CacheHandler<String, AlbumInfo>() {
			@Override
			public void onCacheUpdated(String k, AlbumInfo v) {
				albumLabel.setText(v.getName());
			}
		});
		Controller.getAlbumCache().obtain(s.getAlbumId());
//		model.getAlbumCache().obtain(s.getAlbumId());
		grid.setWidget(row, column, albumLabel);
	}
	
	private void setArtist(int row, int column, SongInfo s) {
		final Label artistLabel = new Label();
		artistLabel.getElement().addClassName(style.leftmargin());
		Controller.getArtistCache().addHandler(s.getArtistId(), new CacheHandler<String, ArtistInfo>() {
//		model.getArtistCache().addHandler(s.getArtistId(), new CacheHandler<String, ArtistInfo>() {
			@Override
			public void onCacheUpdated(String k, ArtistInfo v) {
				artistLabel.setText(v.getName());
			}
		});
		Controller.getArtistCache().obtain(s.getArtistId());
//		model.getArtistCache().obtain(s.getArtistId());
		grid.setWidget(row, column, artistLabel);
	}
	
	// +++ Handlers +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private class MyAddAtEndHandler implements AddAtEndButtonHandler {
		SongInfo song;
		public MyAddAtEndHandler(SongInfo s) {
			this.song = s;
		}
		
		@Override
		public void onAddAtEnd() {
			model.addToCurrentPlaylist(song);
			Controller.getPlayer().addSongToPlaylist(song);
		}
	}


}

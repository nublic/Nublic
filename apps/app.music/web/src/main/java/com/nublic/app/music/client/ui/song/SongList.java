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
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.controller.Controller;
import com.nublic.app.music.client.datamodel.AlbumInfo;
import com.nublic.app.music.client.datamodel.ArtistInfo;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.app.music.client.datamodel.handlers.AddAtEndButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.DeleteButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.EditButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.PlayButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.SongHandler;
import com.nublic.app.music.client.ui.ButtonLine;
import com.nublic.app.music.client.ui.ButtonLineParam;
import com.nublic.app.music.client.ui.ButtonType;
import com.nublic.app.music.client.ui.EmptyWidget;
import com.nublic.app.music.client.ui.dnd.DraggableSong;
import com.nublic.app.music.client.ui.song.AlbumSongList.MyDeleteHandler;
import com.nublic.util.cache.Cache;
import com.nublic.util.cache.CacheHandler;
import com.nublic.util.messages.DefaultComparator;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceIgnorer;
import com.nublic.util.range.Range;

public abstract class SongList extends Composite implements ScrollHandler {
	private static SongListUiBinder uiBinder = GWT.create(SongListUiBinder.class);
	interface SongListUiBinder extends UiBinder<Widget, SongList> { }
	
	// CSS Styles defined in the .xml file
	interface SongStyle extends CssResource {
		String alignright();
		String alignmiddle();
		String leftmargin();
		String rightmargin();
		String bottommargin();
	}

	@UiField SongStyle style;
	@UiField Grid grid;
	Panel scrollPanel;
	int numberOfSongs;	
	
	SequenceIgnorer<Message> sendHelper = new SequenceIgnorer<Message>(DefaultComparator.INSTANCE);
	List<SongLocalizer> unloadedLocalizers;
	HashMap<Integer, SongLocalizer> localizerIndex;
	List<Range> askedRanges = new ArrayList<Range>();
	MySongHandler songHandler;
	
	// With numberOfSongs == -1 we'll get it from first request
	// Scroll panel which we in are in to handle lazy loading
	public SongList(int numberOfSongs, Panel scrollPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.numberOfSongs = numberOfSongs;
		this.scrollPanel = scrollPanel;

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

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				onScroll(null);
			}
		});
	}

	// +++ Things related to lazy loading +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private class MySongHandler implements SongHandler {
		@Override
		public void onSongsChange(int total, int from, int to, List<SongInfo> answerList) {
			if (total != SongList.this.numberOfSongs) {
				// If this happens it means playlist have been changed from other place while we're looking at it
				// TODO: cry
			} else {
				int currentSong = from;
				for (SongInfo s : answerList) {
					_setSong(currentSong, s);
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
				askForsongs(r.getFrom(), r.getTo());
			}
		}
	}
	
	// Get songs properly from model
	public abstract void askForsongs(int from, int to);
	
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
	
	public void updateRangesFromDelete(int deletedRow) {
		Range.removeIntAndShift(askedRanges, deletedRow);
		numberOfSongs--;
	}

	// TODO: make a function to invalid askedRange if request fails
	
	// +++ Things related to ui fill +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public void _setSong(int row, SongInfo s) {
		grid.getRowFormatter().getElement(row).addClassName("translucidPanel");
		setSong(row, s);
	}
	
	public abstract void setSong(int row, SongInfo s);
	protected abstract void prepareGrid();

	// +++ Methods to fill the grid +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	protected void setLenght(int row, int column, String formattedLength) {
		Label titleLabel = new Label(formattedLength);
		titleLabel.getElement().addClassName(style.leftmargin());
		grid.setWidget(row, column, titleLabel);
	}

	protected void setTitle(int row, int column, String title) {
		Label titleLabel = new Label(title);
		titleLabel.getElement().addClassName(style.leftmargin());
		grid.setWidget(row, column, titleLabel);
	}

	protected void setButtons(int row, int column, SongInfo s, PlayButtonHandler pbh, DeleteButtonHandler dbh) {
		ButtonLine buttonLine = new ButtonLine(EnumSet.of(ButtonLineParam.PLAY, ButtonLineParam.DELETE),
											   EnumSet.of(ButtonType.PLAY_SONG, ButtonType.DELETE_PLAYLIST_SONG));
		buttonLine.setPlayButtonHandler(pbh);
		buttonLine.setDeleteButtonHandler(dbh);
		grid.setWidget(row, column, buttonLine);
	}

	protected void setTrackNumber(int row, int column, int track) {
		String trackStr = track == -1 ? "-" : String.valueOf(track);
		Label trackNumLabel = new Label(trackStr);
		trackNumLabel.getElement().addClassName(style.alignright());
		HorizontalPanel capsule = new HorizontalPanel();
		capsule.setWidth("100%");
		capsule.add(trackNumLabel);
		grid.setWidget(row, column, capsule);
	}


	protected void setTitleLenght(int row, int column, SongInfo s, AddAtEndButtonHandler aaebh, PlayButtonHandler pbh, EditButtonHandler ebh, MyDeleteHandler mdh) {
		Label titleLabel = new Label(s.getTitle() + " (" +  s.getFormattedLength() + ")");
		EnumSet<ButtonLineParam> set = EnumSet.of(ButtonLineParam.PLAY, ButtonLineParam.ADD_AT_END, ButtonLineParam.EDIT);
		EnumSet<ButtonType> typeSet = EnumSet.of(ButtonType.PLAY_SONG, ButtonType.EDIT_SONG);
		if (mdh != null) {
			set.add(ButtonLineParam.DELETE);
			typeSet.add(ButtonType.DELETE_COLLECTION_SONG);
		}
		ButtonLine buttonLine = new ButtonLine(set, typeSet);
		buttonLine.setAddAtEndButtonHandler(aaebh);
		buttonLine.setPlayButtonHandler(pbh);
		buttonLine.setEditButtonHandler(ebh);
		if (mdh != null) {
			buttonLine.setDeleteButtonHandler(mdh);
		}
		HorizontalPanel h = new HorizontalPanel();
		h.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		h.add(titleLabel);
		h.add(buttonLine);
		h.getElement().addClassName(style.leftmargin());
		grid.setWidget(row, column, h);
	}

	protected void setAlbum(int row, int column, SongInfo s) {
		final Label albumLabel = new Label();
		albumLabel.getElement().addClassName(style.leftmargin());
		Cache<String, AlbumInfo> albumCache = Controller.INSTANCE.getModel().getAlbumCache();
		albumCache.addHandler(s.getAlbumId(), new CacheHandler<String, AlbumInfo>() {
			@Override
			public void onCacheUpdated(String k, AlbumInfo v) {
				albumLabel.setText(v.getName());
			}
		});
		albumCache.obtain(s.getAlbumId());
		grid.setWidget(row, column, albumLabel);
	}
	
	protected void setArtist(int row, int column, SongInfo s) {
		final Label artistLabel = new Label();
		artistLabel.getElement().addClassName(style.leftmargin());
		Cache<String, ArtistInfo> artistCache = Controller.INSTANCE.getModel().getArtistCache();
		artistCache.addHandler(s.getArtistId(), new CacheHandler<String, ArtistInfo>() {
			@Override
			public void onCacheUpdated(String k, ArtistInfo v) {
				artistLabel.setText(v.getName());
			}
		});
		artistCache.obtain(s.getArtistId());
		grid.setWidget(row, column, artistLabel);
	}
	
	protected void setGrabber(int row, int column, SongInfo s) {
//		HTML grabber = new HTML("[Grab me]");
		DraggableSong grabber = new DraggableSong(row, s);
		grid.setWidget(row, column, grabber);
		Controller.INSTANCE.makeDraggable(grabber);
	}
	
	protected void updateEmptyness() {
		if (grid.getRowCount() <= 0) {
			scrollPanel.add(new EmptyWidget());
		}
	}

}

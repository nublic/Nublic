package com.nublic.app.music.client.ui.song;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Grid;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.datamodel.AlbumInfo;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.app.music.client.datamodel.handlers.SongHandler;
import com.nublic.app.music.client.datamodel.messages.SongMessage;
import com.nublic.app.music.client.ui.ButtonLine;
import com.nublic.app.music.client.ui.ButtonLineParam;
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
	}

	@UiField SongStyle style;
	@UiField Grid grid;
	AlbumInfo album = null;
	Widget scrollPanel;
	
	SequenceIgnorer<Message> sendHelper = new SequenceIgnorer<Message>(DefaultComparator.INSTANCE);
	List<SongLocalizer> unloadedLocalizers;
	SongLocalizer[] localizerIndex;
	List<Range> askedRanges = new ArrayList<Range>();
	

	// Behaviour 1... inside an album...........................................................................
	public SongList(AlbumInfo a, Widget scrollPanel) {
		// Scroll panel which we in are in to handle lazy loading
		initWidget(uiBinder.createAndBindUi(this));
		this.album = a;
		this.scrollPanel = scrollPanel;
		
		prepareGrid();
	
//		album.prepareToAddSongs();
//		album.addSongsChangeHandler(new SongsChangeHandler() {
//			@Override
//			public void onSongsChange(int from, int to) {
//				for (int i = from; i <= to; i++) {
//					setSong(i, album.getSong(i));
//					// Maybe it's more efficient to remove all localizers applying a filter from <= .getPosition <= to
//					SongLocalizer loc = localizerIndex[i];
//					unloadedLocalizers.remove(loc);
//				}
//			}
//		});
//		
//		// Fake widgets which know if are being shown to be replaced onScroll
//		prepareLocalizers(album.getInfo().getNumberOfSongs());		
	}

	private void prepareGrid() {
		grid.resize(album.getNumberOfSongs(), 2);
		grid.getColumnFormatter().setWidth(0, Constants.FIRST_COLUMN_WIDTH);
	}

	private void prepareLocalizers(int amount) {
		localizerIndex = new SongLocalizer[amount];
		unloadedLocalizers = new ArrayList<SongLocalizer>(amount);
		for (int i = 0; i < amount; i++) {
			SongLocalizer loc = new SongLocalizer(i);
			unloadedLocalizers.add(loc);
			localizerIndex[i] = loc;
			grid.setWidget(i, 0, loc);
		}
		scrollPanel.addDomHandler(this, ScrollEvent.getType());
		
		// Couldn't find a way to do it on some widget event in Artist Widget (attachedHandler and LoadHandler don't work)
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				// This is executed when current events stack empties
				// Call onScroll when the widgets are loaded, so content of shown ones can be lazy loaded
				onScroll(null);
			}
		});
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
				needToLoad = true;
				// If we find it we'll construct a request asking for the previous 10 to it and next 30 (if are unloaded and not waiting for answer)
				rangeToAsk.add(findRangeFromPosition(sl.getPosition()));
				trimRangeToAsk(rangeToAsk);
			}
		}
		if (needToLoad) {
//			for (Range r : rangeToAsk) {
//				SongMessage sm = new SongMessage(r.getFrom(), r.getTo(), album);
//				askedRanges.add(r);
//				sendHelper.send(sm, RequestBuilder.GET);
//			}
		}
	}
	
	private Range findRangeFromPosition(int position) {
		int unboundedFrom = position - Constants.PREVIOUS_SONGS_TO_ASK;
		int from = unboundedFrom <= 0 ? 0 : unboundedFrom;
		int unboundedTo = position + Constants.NEXT_SONGS_TO_ASK;
		int to = unboundedTo >= album.getNumberOfSongs() ? album.getNumberOfSongs() -1 : unboundedTo;
		return new Range(from, to);
	}
	
	private void trimRangeToAsk(List<Range> rangeToAsk) {
		for (Range r : askedRanges) {
			Range.remove(rangeToAsk, r);
		}
	}

	// TODO: make a function to invalid askedRange if request fails
	
	public void setSong(int row, SongInfo s) {
		// Column 0
		String trackStr = s.getTrack() == -1 ? "-" : String.valueOf(s.getTrack());
		Label trackNumLabel = new Label(trackStr);
		trackNumLabel.getElement().addClassName(style.alignright());
		HorizontalPanel capsule = new HorizontalPanel();
		capsule.setWidth("100%");
		capsule.setHeight("25px");
		capsule.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		capsule.add(trackNumLabel);
		grid.setWidget(row, 0, capsule);
		
		// Column 1
		Label titleLabel = new Label(s.getTitle() + " (" +  s.getFormattedLength() + ")");
		ButtonLine buttonLine = new ButtonLine(EnumSet.of(ButtonLineParam.PLAY, ButtonLineParam.ADD_AT_END, ButtonLineParam.EDIT));
		HorizontalPanel h = new HorizontalPanel();
		h.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		h.add(titleLabel);
		h.add(buttonLine);
		h.getElement().addClassName("translucidPanel");
		h.getElement().addClassName(style.leftmargin());
		grid.setWidget(row, 1, h);
		
		// Column 2
//		final Label albumLabel = new Label();
//		model.getAlbumCache().addHandler(s.getAlbumId(), new CacheHandler<String, AlbumInfo>() {
//			@Override
//			public void onCacheUpdated(String k, AlbumInfo v) {
//				albumLabel.setText(v.getName());
//			}
//		});
//		model.getAlbumCache().obtain(s.getAlbumId());
//		grid.setWidget(row, 1, albumLabel);
		
		// Column 3
//		final Label artistLabel = new Label();
//		model.getArtistCache().addHandler(s.getArtistId(), new CacheHandler<String, ArtistInfo>() {
//			@Override
//			public void onCacheUpdated(String k, ArtistInfo v) {
//				artistLabel.setText(v.getName());
//			}
//		});
//		model.getArtistCache().obtain(s.getArtistId());
//		grid.setWidget(row, 2, artistLabel);
	}
	
	// Behaviour 2... directly on model...........................................................................
	DataModel model;
	int numberOfSongs;
	
	public SongList(DataModel model, Widget scrollPanel, String artistId, String albumId, String collectionId, int numberOfSongs) {
		initWidget(uiBinder.createAndBindUi(this));
		this.model = model;
		this.numberOfSongs = numberOfSongs;
		
		if (numberOfSongs == -1) {
			
		} else {
			
		}
	}

}

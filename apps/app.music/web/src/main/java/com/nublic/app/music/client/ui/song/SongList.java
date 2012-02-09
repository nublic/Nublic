package com.nublic.app.music.client.ui.song;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Grid;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.datamodel.Album;
import com.nublic.app.music.client.datamodel.Song;
import com.nublic.app.music.client.datamodel.handlers.SongsChangeHandler;
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
	
	@UiField Grid grid;
	Album album;
	Widget scrollPanel;
	SequenceIgnorer<Message> sendHelper = new SequenceIgnorer<Message>(DefaultComparator.INSTANCE);
	List<SongLocalizer> unloadedLocalizers;
	SongLocalizer[] localizerIndex;
	List<Range> askedRanges = new ArrayList<Range>();
	

	public SongList(Album a, Widget scrollPanel) {
		// TODO: pass scroll panel in which we are in to handle lazy loading
		// And number of songs
		initWidget(uiBinder.createAndBindUi(this));
		this.album = a;
		this.scrollPanel = scrollPanel;
		
		prepareGrid();
		
		// Fake widgets which know if are being shown to be replaced onScroll
		prepareLocalizers(album.getNumberOfSongs());
	
		album.prepareToAddSongs();
		album.addSongsChangeHandler(new SongsChangeHandler() {
			@Override
			public void onSongsChange(int from, int to) {
				for (int i = from; i <= to; i++) {
					setSong(i, album.getSong(i));
					// Maybe it's more efficient to remove all localizers applying a filter from <= .getPosition < to
					SongLocalizer loc = localizerIndex[i];
					unloadedLocalizers.remove(loc);
				}
			}
		});
	}
	
	public SongList(String artistId, String albumId, String collectionId, int numberOfSongs) {
	}

	private void prepareGrid() {
		grid.resize(album.getNumberOfSongs(), 3);
		grid.getColumnFormatter().setWidth(0, "100px");
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
			for (Range r : rangeToAsk) {
				SongMessage sm = new SongMessage(r.getFrom(), r.getTo(), album);
				askedRanges.add(r);
				sendHelper.send(sm, RequestBuilder.GET);
			}
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
	
	public void setSong(int row, Song s) {
		Label titleLabel = new Label(s.getTitle());
		ButtonLine buttonLine = new ButtonLine(EnumSet.of(ButtonLineParam.PLAY, ButtonLineParam.EDIT));
		HorizontalPanel h = new HorizontalPanel();
		h.add(titleLabel);
		h.add(buttonLine);
		h.getElement().addClassName("translucidPanel");
		
		grid.setWidget(row, 1, h);
	}

}

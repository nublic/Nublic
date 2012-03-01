package com.nublic.app.music.client.ui.player;

import java.util.ArrayList;
import java.util.List;

import com.bramosystems.oss.player.core.client.skin.CSSSeekBar;
import com.bramosystems.oss.player.core.event.client.SeekChangeHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.nublic.app.music.client.datamodel.SongInfo;

public class PlayerLayout extends Composite {
	private static PlayerLayoutUiBinder uiBinder = GWT.create(PlayerLayoutUiBinder.class);
	interface PlayerLayoutUiBinder extends UiBinder<Widget, PlayerLayout> { }

	@UiField PushButton prevButton;
	@UiField PushButton playButton;
	@UiField PushButton pauseButton;
	@UiField PushButton nextButton;
	@UiField(provided=true) CSSSeekBar seekBar = new CSSSeekBar(10); // create a seekbar with CSS styling ...
	@UiField Label currentTime;
	@UiField Label totalDurationLabel;
	double totalDuration = 0;

	// Handlers
	List<PlayHandler> playHandlers = new ArrayList<PlayHandler>();
	List<PauseHandler> pauseHandlers = new ArrayList<PauseHandler>();
	List<PrevHandler> prevHandlers = new ArrayList<PrevHandler>();
	List<NextHandler> nextHandlers = new ArrayList<NextHandler>();
	
	
	public PlayerLayout() {
		initWidget(uiBinder.createAndBindUi(this));
		
		setPlaying(false);
	}
	
	public void setPlaying(boolean p) {
		playButton.setVisible(!p);
		pauseButton.setVisible(p);
	}
	
	public void setLoadingProgress(double progress) {
		seekBar.setLoadingProgress(progress);		
	}
	
	public void setCurrentProgress(double playPosition) {
		if (totalDuration > 1) {
			seekBar.setPlayingProgress(playPosition / totalDuration);
			currentTime.setText(SongInfo.getFormattedLength(playPosition/1000));
		} else {
			totalDurationLabel.setText("0:00");
			currentTime.setText("0:00");
		}

	}
	
	public void setTotalTime(double totalTime) {
		totalDuration = totalTime;
		totalDurationLabel.setText(SongInfo.getFormattedLength(totalTime/1000));
	}

	// Add handlers
	public void addSeekChangeHandler(SeekChangeHandler h) { seekBar.addSeekChangeHandler(h); }
	public void addPlayHandler(PlayHandler h) { playHandlers.add(h); }
	public void addPauseHandler(PauseHandler h) { pauseHandlers.add(h); }
	public void addPrevHandler(PrevHandler h) { prevHandlers.add(h); }
	public void addNextHandler(NextHandler h) { nextHandlers.add(h); }
	
	@UiHandler("prevButton")
	void onPrevButtonClick(ClickEvent event) {
		for (PrevHandler h : prevHandlers) {
			h.onPrev();
		}
	}

	@UiHandler("playButton")
	void onPlayButtonClick(ClickEvent event) {
		for (PlayHandler h : playHandlers) {
			h.onPlay();
		}
	}
	
	@UiHandler("pauseButton")
	void onPauseButtonClick(ClickEvent event) {
		for (PauseHandler h : pauseHandlers) {
			h.onPause();
		}
	}

	@UiHandler("nextButton")
	void onNextButtonClick(ClickEvent event) {
		for (NextHandler h : nextHandlers) {
			h.onNext();
		}
	}
}

package com.nublic.app.music.client.ui.player;

import java.util.ArrayList;
import java.util.List;

import com.bramosystems.oss.player.core.client.skin.CSSSeekBar;
import com.bramosystems.oss.player.core.event.client.SeekChangeHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.kiouri.sliderbar.client.event.BarValueChangedEvent;
import com.kiouri.sliderbar.client.event.BarValueChangedHandler;
import com.kiouri.sliderbar.client.solution.simplevertical.SliderBarSimpleVertical;
import com.nublic.app.music.client.Resources;
import com.nublic.app.music.client.datamodel.AlbumInfo;
import com.nublic.app.music.client.datamodel.ArtistInfo;
import com.nublic.app.music.client.datamodel.Controller;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.util.cache.Cache;
import com.nublic.util.cache.CacheHandler;

public class PlayerLayout extends Composite {
	private static PlayerLayoutUiBinder uiBinder = GWT.create(PlayerLayoutUiBinder.class);
	interface PlayerLayoutUiBinder extends UiBinder<Widget, PlayerLayout> { }

	// CSS Styles defined in the .xml file
	interface PlayerStyle extends CssResource {
		String background();
		String nobackground();
		String toggled();
		String marginleft();
		String moremarginleft();
		String smallround();
		String biground();
		String glow();
	}

	@UiField PlayerStyle style;
	// UI
	@UiField PushButton prevButton;
	@UiField PushButton playButton;
	@UiField PushButton pauseButton;
	@UiField PushButton nextButton;
	@UiField(provided=true) CSSSeekBar seekBar = new CSSSeekBar(10); // create a seekbar with CSS styling ...
//	@UiField(provided=true) VolumeControl volumeControl = new VolumeControl(new Image(Resources.INSTANCE.volume()), 10);
	@UiField(provided=true) SliderBarSimpleVertical volumeControl = new SliderBarSimpleVertical(100, "50px", false);
	
	@UiField Label currentTime;
	@UiField Label totalDurationLabel;
	@UiField Label artistLabel;
	@UiField Label albumLabel;
	@UiField Label songLabel;
	@UiField Image albumArt;
	@UiField PushButton shufleButton;
	@UiField PushButton repeatButton;
	@UiField PushButton volumeButton;

	// Handlers
	List<PauseHandler> pauseHandlers = new ArrayList<PauseHandler>();
	List<PlayHandler> playHandlers = new ArrayList<PlayHandler>();
	List<PrevHandler> prevHandlers = new ArrayList<PrevHandler>();
	List<NextHandler> nextHandlers = new ArrayList<NextHandler>();
	List<VolumeHandler> volumeHandlers = new ArrayList<VolumeHandler>();
	List<ShufleHandler> shufleHandlers = new ArrayList<ShufleHandler>();
	List<RepeatHandler> repeatHandlers = new ArrayList<RepeatHandler>();
	
	double totalDuration = 0;
	boolean shufleActive = false;
	boolean repeatActive = false;
	boolean volumeActive = false;
	
	public PlayerLayout() {
		initWidget(uiBinder.createAndBindUi(this));
		
		setPlaying(false);
		this.addAttachHandler(new Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached()) {
					initVolumeControl();
				}
			}
		});
	}
	
	private void initVolumeControl() {
		volumeControl.addBarValueChangedHandler(new BarValueChangedHandler() {
			@Override
			public void onBarValueChanged(BarValueChangedEvent event) {
				for (VolumeHandler h : volumeHandlers) {
					h.onVolumeChange(1 - (double)event.getValue()/100.0);
				}
			}
		});
		volumeControl.setVisible(false);
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
	
	public void setSongInfo(SongInfo s) {
		if (s == null) {
			setTotalTime(0);
			artistLabel.setVisible(false);
			albumLabel.setVisible(false);
			songLabel.setVisible(false);
			albumArt.setVisible(false);
		} else {
			setTotalTime(s.getLength() * 1000);
			Cache<String, ArtistInfo> artistCache = Controller.getModel().getArtistCache();
			artistCache.addHandler(s.getArtistId(), new CacheHandler<String, ArtistInfo>() {
				@Override
				public void onCacheUpdated(String k, ArtistInfo v) {
					artistLabel.setText(v.getName());
					artistLabel.setVisible(true);
				}
			});
			artistCache.obtain(s.getArtistId());
			Cache<String, AlbumInfo> albumCache = Controller.getModel().getAlbumCache();
			albumCache.addHandler(s.getAlbumId(), new CacheHandler<String, AlbumInfo>() {
				@Override
				public void onCacheUpdated(String k, AlbumInfo v) {
					albumLabel.setText(v.getName());
					albumLabel.setVisible(true);
				}
			});
			albumCache.obtain(s.getAlbumId());
			songLabel.setText(s.getTitle());
			songLabel.setVisible(true);
			setImage(s.getAlbumId());
		}
	}
	
	private void setImage(String albumId) {
		if (albumId == null) {
			albumArt.setVisible(false);
		} else {
			albumArt.addErrorHandler(new ErrorHandler() {
				@Override
				public void onError(ErrorEvent event) {
					albumArt.setResource(Resources.INSTANCE.album());
					albumArt.setVisible(true);
				}
			});
			albumArt.addLoadHandler(new LoadHandler() {
				@Override
				public void onLoad(LoadEvent event) {
					albumArt.setVisible(true);					
				}
			});
			albumArt.setUrl(AlbumInfo.getAlbumImageUrl(albumId));
		}
	}

	public void setTotalTime(double totalTime) {
		totalDuration = totalTime;
		totalDurationLabel.setText(SongInfo.getFormattedLength(totalTime/1000));
	}

	// Add handlers
	public void addSeekChangeHandler(SeekChangeHandler h) { seekBar.addSeekChangeHandler(h); }
	public void addVolumeHandler(VolumeHandler h) { volumeHandlers.add(h); }
	public void addPlayHandler(PlayHandler h) { playHandlers.add(h); }
	public void addPauseHandler(PauseHandler h) { pauseHandlers.add(h); }
	public void addPrevHandler(PrevHandler h) { prevHandlers.add(h); }
	public void addNextHandler(NextHandler h) { nextHandlers.add(h); }
	public void addShufleHandler(ShufleHandler h) { shufleHandlers.add(h); }
	public void addRepeatHandler(RepeatHandler h) { repeatHandlers.add(h); }
	
	// Primary buttons handlers
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
	
	// Secondary buttons handlers
	@UiHandler("shufleButton")
	void onShufleButtonClick(ClickEvent event) {
		shufleActive = toggleButton(shufleButton, shufleActive);
		for (ShufleHandler h : shufleHandlers) {
			h.onShufleToggled(shufleActive);
		}
	}

	@UiHandler("repeatButton")
	void onRepeatButtonClick(ClickEvent event) {
		repeatActive = toggleButton(repeatButton, repeatActive);
		for (RepeatHandler h : repeatHandlers) {
			h.onRepeatToggled(repeatActive);
		}
	}

	@UiHandler("volumeButton")
	void onVolumeButtonClick(ClickEvent event) {
		volumeActive = toggleButton(volumeButton, volumeActive);
		volumeControl.setVisible(volumeActive);
		volumeControl.setFocus(true);
	}
	
	@UiHandler("volumeControl")
	void onVolumeControlBlur(BlurEvent event) {
		volumeControl.setVisible(false);
		volumeActive = toggleButton(volumeButton, volumeActive);
	}

	private boolean toggleButton(PushButton b, boolean active) {
		if (active) {
			b.getElement().removeClassName(style.toggled());
		} else {
			b.getElement().addClassName(style.toggled());
		}
		return !active;
	}
}

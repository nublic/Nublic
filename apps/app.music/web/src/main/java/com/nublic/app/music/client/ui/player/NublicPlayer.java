package com.nublic.app.music.client.ui.player;

import java.util.ArrayList;
import java.util.List;

import com.bramosystems.oss.player.core.client.LoadException;
import com.bramosystems.oss.player.core.client.PlayException;
import com.bramosystems.oss.player.core.client.PlayerUtil;
import com.bramosystems.oss.player.core.client.Plugin;
import com.bramosystems.oss.player.core.client.PluginNotFoundException;
import com.bramosystems.oss.player.core.client.PluginVersionException;
import com.bramosystems.oss.player.core.client.RepeatMode;
import com.bramosystems.oss.player.core.client.skin.CustomAudioPlayer;
import com.bramosystems.oss.player.core.event.client.PlayStateEvent;
import com.bramosystems.oss.player.core.event.client.PlayStateHandler;
import com.bramosystems.oss.player.core.event.client.PlayerStateEvent;
import com.bramosystems.oss.player.core.event.client.PlayerStateHandler;
import com.bramosystems.oss.player.core.event.client.SeekChangeEvent;
import com.bramosystems.oss.player.core.event.client.SeekChangeHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.util.error.ErrorPopup;


public class NublicPlayer extends CustomAudioPlayer {
	PlayerLayout controls;
	List<SongInfo> playlist = new ArrayList<SongInfo>();
	Timer timer;
	PlayStateEvent lastStateEvent;
	boolean isShuffleEnabled = false;
	
	public static Widget create() {
		try {
			return new NublicPlayer(Plugin.FlashPlayer);
		} catch (Exception e) {
			try {
				return new NublicPlayer(Plugin.Native);
			} catch (PluginNotFoundException e2) {
				return PlayerUtil.getMissingPluginNotice(e2.getPlugin());
			} catch (Exception e3) {
				return new Label(e3.getMessage());
			}
		}
	}
	
	public NublicPlayer(Plugin p) throws PluginNotFoundException, PluginVersionException, LoadException {
		super(p, GWT.getModuleBaseURL() + "void.mp3", false, "65px", "850px");

		controls = new PlayerLayout();
		addControlHandlers();				// For buttons clicks
		setPlayerControlWidget(controls);	// Set player widget showing
		addPlayHandler();					// Control what happens when state changes (stop, play, ..)
		setTimer();							// Monitor playing progress & update timer display ...
		addPlayerHandler();					// Clear the initial playlist
	}
	
	public PlayStateEvent getLastEvent() { return lastStateEvent; }
	
	private void addPlayerHandler() {
		addPlayerStateHandler(new PlayerStateHandler() {
			@Override
			public void onPlayerStateChanged(PlayerStateEvent event) {
				if (event.getPlayerState() == PlayerStateEvent.State.Ready) {
					clearPlaylist();
					setVolume(1);
				}
			}
		});
	}

	private void setTimer() {
        timer = new Timer() {
            @Override
            public void run() {
            	// Playing progress
            	controls.setCurrentProgress(getPlayPosition());
            	// Loading progres
            	double loadingProgress = (getMediaDuration() / controls.getTotalDuration());
                controls.setLoadingProgress(loadingProgress);
            }
        };
	}

	private void addPlayHandler() {
		// update controls based on playback state ...
        addPlayStateHandler(new PlayStateHandler() {
            @Override
            public void onPlayStateChanged(PlayStateEvent event) {
            	SongInfo song = playlist.get(event.getItemIndex());
            	lastStateEvent = event;
            	switch (event.getPlayState()) {
            	case Paused:
            		controls.setPlaying(false);
            		timer.cancel();
            		break;
            	case Started:
            		controls.setPlaying(true);
            		controls.setSongInfo(song);
            		timer.scheduleRepeating(Constants.UPDATE_SAMPLE_MILLISECONDS);
            		break;
            	case Stopped:
            		controls.setPlaying(false);
            		timer.cancel();
            		break;
            	case Finished:
            		controls.setPlaying(false);
            		controls.setCurrentProgress(0);
            		controls.setSongInfo(null);
            		controls.setTotalDuration(0);
            		timer.cancel();
            		break;
               }
            }
        });
	}
	
	private void addControlHandlers() {
		controls.addPauseHandler(new PauseHandler() {
			@Override
			public void onPause() {
				pauseMedia();
			}
		});
		controls.addPlayHandler(new PlayHandler() {
			@Override
			public void onPlay() {
				nublicPlay();
			}
		});
		controls.addNextHandler(new NextHandler() {
			@Override
			public void onNext() {
				nublicPlayNext();			
			}
		});
		controls.addPrevHandler(new PrevHandler() {
			@Override
			public void onPrev() {
				nublicPlayPrev();			
			}
		});
		controls.addSeekChangeHandler(new SeekChangeHandler() {
			@Override
			public void onSeekChanged(SeekChangeEvent event) {
				setPlayPosition(event.getSeekPosition() * playlist.get(lastStateEvent.getItemIndex()).getLength() * 1000);
			}
		});
		controls.addVolumeHandler(new VolumeHandler() {
			@Override
			public void onVolumeChange(double newVolume) {
				setVolume(newVolume);
			}
		});
		controls.addShufleHandler(new ShuffleHandler() {
			@Override
			public void onShuffleToggled(boolean active) {
				setShuffleEnabled(active);		
				isShuffleEnabled = active;
			}
		});
		controls.addRepeatHandler(new RepeatHandler() {
			@Override
			public void onRepeatToggled(boolean active) {
				if (active) {
					setRepeatMode(RepeatMode.REPEAT_ALL);
				} else {
					setRepeatMode(RepeatMode.REPEAT_OFF);
				}
			}
		});
	}
	
	
	// Utils
	public void addSongToPlaylist(SongInfo song) {
		playlist.add(song);
		if (getPlaylistSize() == 0) {
			try {
				loadMedia(song.getUrl());
			} catch (LoadException e) {
				ErrorPopup.showError(e.getMessage());
				e.printStackTrace();
			}
		} else {
			addToPlaylist(song.getUrl());
		}
	}
	
	public void addSongsToPlaylist(List<SongInfo> songList) {
		for (SongInfo s : songList) {
			addSongToPlaylist(s);
		}
	}
	
	public int getNublicPlaylistSize() {
		return playlist.size();
	}

	public void clearNublicPlaylist() {
		if (!playlist.isEmpty()) {
			stopMedia();
			playlist.clear();
			clearPlaylist();
		}
	}

	public void playSong(int index) {
		if (isShuffleEnabled) {
			setShuffleEnabled(false);
			play(index);
			setShuffleEnabled(true);
		} else {
			play(index);			
		}
	}
	
	// secure play methods
	public void nublicPlayNext() {
		try {
			if (lastStateEvent != null && lastStateEvent.getItemIndex() != getPlaylistSize() - 1) {
				playNext();
			}
		} catch (PlayException e) {
			ErrorPopup.showError(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void nublicPlayPrev() {
		try {
			if (lastStateEvent != null && lastStateEvent.getItemIndex() != 0) {
				playPrevious();
			}
		} catch (PlayException e) {
			ErrorPopup.showError(e.getMessage());
			e.printStackTrace();
		}	
	}
	
	public void nublicPlay() {
		try {
			if (getPlaylistSize() != 0) {
				playMedia();
			}
		} catch (Exception e) {
			ErrorPopup.showError(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void nublicStop() {
		if (!playlist.isEmpty()) {
			stopMedia();
		}
	}
}

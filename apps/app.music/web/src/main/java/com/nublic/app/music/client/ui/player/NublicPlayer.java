package com.nublic.app.music.client.ui.player;

import java.util.ArrayList;
import java.util.List;

import com.bramosystems.oss.player.core.client.LoadException;
import com.bramosystems.oss.player.core.client.PlayException;
import com.bramosystems.oss.player.core.client.PlayerUtil;
import com.bramosystems.oss.player.core.client.Plugin;
import com.bramosystems.oss.player.core.client.PluginNotFoundException;
import com.bramosystems.oss.player.core.client.PluginVersionException;
import com.bramosystems.oss.player.core.client.skin.CustomAudioPlayer;
import com.bramosystems.oss.player.core.event.client.LoadingProgressEvent;
import com.bramosystems.oss.player.core.event.client.LoadingProgressHandler;
import com.bramosystems.oss.player.core.event.client.PlayStateEvent;
import com.bramosystems.oss.player.core.event.client.PlayStateHandler;
import com.bramosystems.oss.player.core.event.client.PlayerStateEvent;
import com.bramosystems.oss.player.core.event.client.PlayerStateEvent.State;
import com.bramosystems.oss.player.core.event.client.PlayerStateHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.util.error.ErrorPopup;


public class NublicPlayer extends CustomAudioPlayer {
	PlayerLayout controls;
	List<SongInfo> playlist = new ArrayList<SongInfo>();
	Timer timer;
	
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
		super(p, GWT.getModuleBaseURL() + "void.mp3", false, "65px", "500px");

		controls = new PlayerLayout();
		addControlHandlers();
		setPlayerControlWidget(controls);
		addPlayHandler();
		addLoadingHandler();
		// monitor playing progress & update timer display ...
		setTimer();
		
		showLogger(true);
		addPlayerStateHandler(new PlayerStateHandler() {
			@Override
			public void onPlayerStateChanged(PlayerStateEvent event) {
				if (event.getPlayerState() == State.Ready) {
					clearPlaylist();
				}
			}
		});
	}
	
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
	

	private void setTimer() {
        timer = new Timer() {
            @Override
            public void run() {
            	controls.setCurrentProgress(getPlayPosition());
            }
        };
	}

	private void addLoadingHandler() {
		// monitor loading progress and indicate on seekbar
        addLoadingProgressHandler(new LoadingProgressHandler() {
            @Override
            public void onLoadingProgress(LoadingProgressEvent event) {
                controls.setLoadingProgress(event.getProgress());
            }
        });
	}

	private void addPlayHandler() {
		// update controls based on playback state ...
        addPlayStateHandler(new PlayStateHandler() {
            @Override
            public void onPlayStateChanged(PlayStateEvent event) {
            	SongInfo song = playlist.get(event.getItemIndex());
            	switch(event.getPlayState()) {
            	case Paused:
            		controls.setPlaying(false);
            		timer.cancel();
            		break;
            	case Started:
            		controls.setPlaying(true);
            		controls.setTotalTime(song.getLength()*1000);
            		timer.scheduleRepeating(900);
            		break;
            	case Stopped:
            		controls.setPlaying(false);
            		timer.cancel();
            		break;
            	case Finished:
            		controls.setPlaying(false);
            		controls.setTotalTime(0);
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
				try {
					if (getPlaylistSize() != 0) {
						playMedia();
					}
				} catch (Exception e) {
					ErrorPopup.showError(e.getMessage());
					e.printStackTrace();
				}
			}
		});
		controls.addNextHandler(new NextHandler() {
			@Override
			public void onNext() {
				try {
					playNext();
				} catch (PlayException e) {
					ErrorPopup.showError(e.getMessage());
					e.printStackTrace();
				}				
			}
		});
		controls.addPrevHandler(new PrevHandler() {
			@Override
			public void onPrev() {
				try {
					playPrevious();
				} catch (PlayException e) {
					ErrorPopup.showError(e.getMessage());
					e.printStackTrace();
				}				
			}
		});
	}
}

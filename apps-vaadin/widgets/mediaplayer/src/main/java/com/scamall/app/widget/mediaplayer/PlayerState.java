package com.scamall.app.widget.mediaplayer;

/**
 * Represents the state in which the player is: playing, stopped...
 * Documentation taken from Projekktor website
 * [http://www.projekktor.com/docs/events]
 * 
 * @author Alejandro Serrano
 */
public enum PlayerState {
	/** The playback component is waiting for user-interaction and
	 *  has not started playback yet. Usually a poster-image is 
	 *  shown at this state. */
	IDLE,
	/** The playback component begins playback for the very first
	 *  time and initializes */
	AWAKENING,
	/** The playback component is currently playing */
	PLAYING,
	/** The playback component has been paused by the user or via an
	 *   external JS API call */
	PAUSED,
	/** The current media item has been played back completely */
	COMPLETED,
	/** The player component buffers new media data in order to allow
	 *  a constant playback */
	BUFFERING,
	/** An error occured */
	ERROR
}
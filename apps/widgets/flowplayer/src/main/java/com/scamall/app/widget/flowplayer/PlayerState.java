package com.scamall.app.widget.flowplayer;

/**
 * Represents the state in which the player is: playing, stopped...
 * 
 * @author Alejandro Serrano
 */
public enum PlayerState {
	/**
	 * Player is buffering an object
	 */
	BUFFERING,
	/**
	 * Player is playing audio or video
	 */
	PLAYING,
	/**
	 * Player is paused
	 */
	PAUSED,
	/**
	 * Player is not playing any multimedia object
	 */
	STOPPED
}
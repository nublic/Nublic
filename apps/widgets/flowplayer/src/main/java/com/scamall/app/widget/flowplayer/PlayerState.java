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
	BUFFERING ("buffering"),
	/**
	 * Player is playing audio or video
	 */
	PLAYING ("playing"),
	/**
	 * Player is paused
	 */
	PAUSED ("paused"),
	/**
	 * Player is not playing any multimedia object
	 */
	STOPPED ("stopped");
	
	
	private String uidl_name;
	
	PlayerState(String uidl_name) {
		this.uidl_name = uidl_name;
	}
	
	/**
	 * @return The name of the state in UIDL.
	 */
	public String serializeToUidl() {
		return this.uidl_name;
	}
	
	/**
	 * Creates a new PlayerState object based on its UIDL name.
	 * @param uidl_name
	 * @return The new player state.
	 * @throws IllegalArgumentException If the name is not valid.
	 */
	public static PlayerState deserializeFromUidl(String uidl_name) {
		for (PlayerState st : PlayerState.values()) {
			if (st.serializeToUidl().equals(uidl_name))
				return st;
		}
		throw new IllegalArgumentException();
	}
}
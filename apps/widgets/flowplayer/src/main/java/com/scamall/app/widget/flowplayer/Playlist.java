/**
 * 
 */
package com.scamall.app.widget.flowplayer;

/**
 * Extends a BasePlaylist to reference a Flowplayer and allow listener methods.
 * 
 * @author Alejandro Serrano
 */
public class Playlist extends BasePlaylist {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4031362629708033634L;

	private Flowplayer player;

	public Playlist(Flowplayer player) {
		super();
		this.player = player;
	}

	public Flowplayer getPlayer() {
		return this.player;
	}
}

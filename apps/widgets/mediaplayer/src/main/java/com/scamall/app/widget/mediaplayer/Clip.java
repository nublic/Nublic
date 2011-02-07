/**
 * 
 */
package com.scamall.app.widget.mediaplayer;

import java.io.Serializable;

/**
 * Represents a clip, that is, a multimedia resource that will be played by
 * Flowplayer.
 * 
 * @author Alejandro Serrano
 */
public class Clip implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6840417551899445042L;

	private String url;
	private String poster_url;

	public Clip(String url) {
		this(url, null);
	}
	
	public Clip(String url, String poster_url) {
		this.url = url;
		this.poster_url = null;
	}

	/**
	 * Gets the clip URL
	 * 
	 * @return The clip URL
	 */
	public String getUrl() {
		return this.url;
	}
	
	/**
	 * Gets the poster URL for this clip
	 * 
	 * @return The poster URL
	 */
	public String getPosterUrl() {
		return this.poster_url;
	}
}
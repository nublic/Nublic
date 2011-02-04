/**
 * 
 */
package com.scamall.app.widget.flowplayer;

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
	private ClipScaling scaling;

	public Clip(String url, ClipScaling scaling) {
		this.url = url;
		this.scaling = scaling;
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
	 * Gets the clip scaling
	 * 
	 * @return The clip scaling
	 */
	public ClipScaling getScaling() {
		return this.scaling;
	}
}
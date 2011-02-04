package com.scamall.app.widget.flowplayer;

/**
 * Specifies the different ways of scaling a clip in Flowplayer
 * 
 * @see http://flowplayer.org/documentation/configuration/clips.html#properties
 * 
 * @author Alejandro Serrano
 */
public enum ClipScaling {
	/**
	 * Corresponds to 'orig'
	 */
	ORIGINAL_SIZE ("orig"),
	/**
	 * Corresponds to 'fit'
	 */
	FIT_PRESERVING_ASPECT_RATIO ("fit"),
	/**
	 * Corresponds to 'scale'
	 */
	FIT_NOT_PRESERVING_ASPECT_RATIO ("scale"),
	/**
	 * Corresponds to 'half'
	 */
	HALF_SIZE ("half");
	
	
	private String flowplayer_name;
	
	ClipScaling(String flowplayer_name) {
		this.flowplayer_name = flowplayer_name;
	}
	
	/**
	 * @return The name of the scaling in Flowplayer.
	 */
	public String getFlowplayerName() {
		return this.flowplayer_name;
	}
}
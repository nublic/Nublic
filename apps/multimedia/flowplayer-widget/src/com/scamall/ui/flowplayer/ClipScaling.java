/**
 * 
 */
package com.scamall.ui.flowplayer;

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
	ORIGINAL_SIZE,
	/**
	 * Corresponds to 'fit'
	 */
	FIT_PRESERVING_ASPECT_RATIO,
	/**
	 * Corresponds to 'scale'
	 */
	FIT_NOT_PRESERVING_ASPECT_RATIO,
	/**
	 * Corresponds to 'half'
	 */
	HALF_SIZE
}

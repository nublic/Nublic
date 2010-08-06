/**
 * 
 */
package com.scamall.ui.flowplayer;

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

	private long id;
	private String url;
	private ClipScaling scaling;

	public Clip(String url, ClipScaling scaling) {
		this.url = url;
		this.scaling = scaling;
	}

	/**
	 * Gets the clip id
	 * 
	 * @return The ID
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the ID
	 * 
	 * @param id
	 *            The ID to set
	 */
	void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets the clip URL
	 * 
	 * @return The clip URL
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Gets the clip scaling
	 * 
	 * @return The clip scaling
	 */
	public ClipScaling getScaling() {
		return scaling;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Clip))
			return false;
		Clip other = (Clip) obj;
		if (id != other.id)
			return false;
		return true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Clip [url=" + url + ", scaling=" + scaling + "]";
	}

}

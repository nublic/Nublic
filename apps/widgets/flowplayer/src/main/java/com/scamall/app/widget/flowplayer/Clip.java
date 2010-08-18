/**
 * 
 */
package com.scamall.app.widget.flowplayer;

import java.io.Serializable;
import java.util.Map;

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

	private int id;
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
	public int getId() {
		return id;
	}

	/**
	 * Sets the ID
	 * 
	 * @param id
	 *            The ID to set
	 */
	void setId(int id) {
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

	/* SERIALIZATION TO UIDL */
	/* ===================== */

	/**
	 * Defines the different attributes that will be serialized to UIDL.
	 */
	static String[] getUidlSerializableAttributes() {
		return new String[] { "id", "url", "scaling" };
	}

	/**
	 * Serializes a clip attribute to UIDL.
	 * 
	 * @param attribute
	 *            Name of the attribute.
	 * @return The serialized value as string.
	 * @throws IllegalArgumentException
	 *             The requested attribute does not exist.
	 */
	String getUidlSerializedAttribute(String attribute) {
		if (attribute.equals("id"))
			return ((Integer) this.id).toString();
		else if (attribute.equals("url"))
			return this.url;
		else if (attribute.equals("scaling"))
			return this.scaling.serializeToUidl();
		else
			throw new IllegalArgumentException();
	}

	/**
	 * Deserializes a clip from its UIDL attributes.
	 * 
	 * @param attributes
	 * @return
	 * @throws IllegalArgumentException
	 *             Some attribute is missing or its type is not correct.
	 */
	static Clip getClipFromUidlAttributes(Map<String, String> attributes) {
		try {
			// Get needed attributes
			String id_uidl = attributes.get("id");
			String url_uidl = attributes.get("url");
			String scaling_uidl = attributes.get("scaling");
			// Build new Clip
			Clip c = new Clip(url_uidl,
					ClipScaling.deserializeFromUidl(scaling_uidl));
			c.setId(Integer.parseInt(id_uidl));
			return c;
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}
}
package com.scamall.app.widget.flowplayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.random.RandomData;
import org.apache.commons.math.random.RandomDataImpl;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ValueMap;

/**
 * Represents a list of clips for a player.
 * 
 * @author Alejandro Serrano
 */
public class BasePlaylist implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3079543358923571742L;

	protected int last_clip_id;
	protected ArrayList<Integer> clip_order;
	protected HashMap<Integer, Clip> clips;
	protected boolean repeat;
	protected boolean random;
	protected int[] random_order;

	public BasePlaylist() {
		this.clip_order = new ArrayList<Integer>();
		this.clips = new HashMap<Integer, Clip>();
		this.last_clip_id = 0;
		this.repeat = true;
		this.random = false;
		this.random_order = new int[0];
	}

	protected void regenerateRandomOrder() {
		if (this.clip_order.isEmpty()) {
			random_order = new int[0];
		} else {
			RandomData data = new RandomDataImpl();
			random_order = data.nextPermutation(this.clip_order.size(),
					this.clip_order.size());
		}
	}
	
	public boolean isRepeat() {
		return this.repeat;
	}
	
	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}
	
	public boolean isRandom() {
		return this.random;
	}
	
	public void setRandom(boolean random) {
		this.random = random;
	}
	
	/* NEXT AND PREVIOUS */
	/* ================= */
	
	private Clip getFirstClip() {
		if (this.isEmpty())
			return null;
		
		if (this.random) {
			return this.get(0);
		} else {
			return this.get(this.random_order[0]);
		}
	}
	
	public Clip getNextClip(Clip clip) {
		// Find clip in the list
		int current_clip_id = clip.getId();
		int clip_pos = this.getClipIndex(current_clip_id);
		// Clip not found
		if (clip_pos == -1) {
			return this.getFirstClip();
		}
		
		if (!random) {
			// We came to the end of the list
			if (clip_pos == (this.size() - 1)) {
				if (this.repeat)
					return this.get(0);
				else
					return null;
			}
			// Normal case, return the next clip in the playlist
			return this.get(clip_pos + 1);
		} else {
			// Search in random order
			int random_pos = ArrayUtils.indexOf(this.random_order, clip_pos);
			// We came to the end of the list
			if (random_pos == (this.size() - 1)) {
				if (this.repeat)
					return this.get(this.random_order[0]);
				else
					return null;
			}
			// Normal case, return the next clip by random order
			return this.get(this.random_order[random_pos + 1]);
		}
	}
	
	public Clip getPreviousClip(Clip clip) {
		// Find clip in the list
		int current_clip_id = clip.getId();
		int clip_pos = this.getClipIndex(current_clip_id);
		// Clip not found
		if (clip_pos == -1) {
			return this.getFirstClip();
		}
		
		if (!random) {
			// We came to the start of the list
			if (clip_pos == 0) {
				if (this.repeat)
					return this.get(this.size() - 1);
				else
					return null;
			}
			// Normal case, return the next clip in the playlist
			return this.get(clip_pos - 1);
		} else {
			// Search in random order
			int random_pos = ArrayUtils.indexOf(this.random_order, clip_pos);
			// We came to the end of the list
			if (random_pos == 0) {
				if (this.repeat)
					return this.get(this.random_order[this.size() - 1]);
				else
					return null;
			}
			// Normal case, return the next clip by random order
			return this.get(this.random_order[random_pos - 1]);
		}
	}

	/* LIST-LIKE INTERFACE */
	/* =================== */

	private int returnNextId() {
		return this.last_clip_id++;
	}

	public void add(Clip clip) {
		int id = this.returnNextId();
		clip.setId(id);
		clip_order.add(id);
		clips.put(id, clip);
		this.regenerateRandomOrder();
	}

	public void add(int index, Clip clip) {
		int id = this.returnNextId();
		clip.setId(id);
		clip_order.add(index, id);
		clips.put(id, clip);
		this.regenerateRandomOrder();
	}

	public void clear() {
		clip_order.clear();
		clips.clear();
		this.regenerateRandomOrder();
	}

	public boolean contains(Clip clip) {
		return clip_order.contains(clip.getId());
	}

	public int indexOf(Clip clip) {
		return clip_order.indexOf(clip.getId());
	}

	public Clip get(int index) {
		return clips.get(clip_order.get(index));
	}

	public Clip getClip(int id) {
		return clips.get(id);
	}

	public int getClipIndex(Clip clip) {
		return this.getClipIndex(clip.getId());
	}

	public int getClipIndex(int clip_id) {
		if (!clip_order.contains(clip_id))
			return -1;

		return clip_order.indexOf(clip_id);
	}

	public boolean isEmpty() {
		return clip_order.isEmpty();
	}

	public PlaylistIterator getIterator() {
		return new PlaylistIterator(this);
	}

	public void remove(int index) {
		long remove_id = clip_order.get(index);
		clips.remove(remove_id);
		clip_order.remove(index);
		this.regenerateRandomOrder();
	}

	public void remove(Clip clip) {
		long remove_id = clip.getId();
		clips.remove(remove_id);
		clip_order.remove(remove_id);
		this.regenerateRandomOrder();
	}

	public int size() {
		return clip_order.size();
	}

	public Clip[] toArray() {
		return (Clip[]) clips.values().toArray();
	}

	class PlaylistIterator implements Iterator<Clip> {

		BasePlaylist playlist;
		Iterator<Integer> order_iterator;
		Clip current_clip;

		public PlaylistIterator(BasePlaylist playlist) {
			this.playlist = playlist;
			this.order_iterator = playlist.clip_order.iterator();
			this.current_clip = null;
		}

		public boolean hasNext() {
			return this.order_iterator.hasNext();
		}

		public Clip next() {
			long next_id = this.order_iterator.next();
			this.current_clip = this.playlist.clips.get(next_id);
			return this.current_clip;
		}

		public void remove() {
			this.playlist.clips.remove(this.current_clip.getId());
			this.playlist.regenerateRandomOrder();
			this.order_iterator.remove();
		}

	}

	/* SERIALIZATION TO UIDL */
	/* ===================== */

	protected static final String UIDL_PREFIX = "clip_";

	@Deprecated
	public void serializeToUidl(PaintTarget target) throws PaintException {
		// Add the clip order
		ArrayList<String> order_serialized = new ArrayList<String>();
		for (Integer clip_id : this.clip_order)
			order_serialized.add(clip_id.toString());
		target.addAttribute(UIDL_PREFIX + "order", order_serialized.toArray());

		// Add each of the clips' attributes
		for (String attribute : Clip.getUidlSerializableAttributes()) {
			HashMap<String, String> attribute_values = new HashMap<String, String>();
			for (Integer clip_id : clips.keySet()) {
				Clip clip = clips.get(clip_id);
				attribute_values.put(clip_id.toString(),
						clip.getUidlSerializedAttribute(attribute));
			}
			target.addAttribute(UIDL_PREFIX + attribute, attribute_values);
		}
	}

	@Deprecated
	public static BasePlaylist deserializeFromUidl(UIDL uidl) {
		BasePlaylist pl = new BasePlaylist();

		// Deserialize clip order
		String[] order = uidl.getStringArrayAttribute(UIDL_PREFIX + "order");
		for (String clip_id : order)
			pl.clip_order.add(Integer.parseInt(clip_id));

		// Deserialize rest of attributes
		HashMap<String, ValueMap> attribute_values = new HashMap<String, ValueMap>();
		for (String attribute : Clip.getUidlSerializableAttributes()) {
			// Get each attribute map
			attribute_values.put(attribute,
					uidl.getMapAttribute(UIDL_PREFIX + attribute));
		}

		// Build each clip
		for (Integer clip_id : pl.clip_order) {
			HashMap<String, String> clip_attributes = new HashMap<String, String>();
			for (String attribute : Clip.getUidlSerializableAttributes()) {
				clip_attributes.put(attribute, attribute_values.get(attribute)
						.getString(clip_id.toString()));
			}
			Clip clip = Clip.getClipFromUidlAttributes(clip_attributes);
			pl.clips.put(clip_id, clip);
		}

		return pl;
	}

}
package com.scamall.app.widget.flowplayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Represents a list of clips for a 
 * 
 * @author Alejandro Serrano 
 */
public class Playlist implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3079543358923571742L;

	Flowplayer player;
	ArrayList<Long> clip_order;
	HashMap<Long, Clip> clips;
	long last_clip_id;

	/**
	 * 
	 */
	public Playlist(Flowplayer player) {
		this.player = player;
		this.clip_order = new ArrayList<Long>();
		this.clips = new HashMap<Long, Clip>();
		this.last_clip_id = 0;
	}

	private long returnNextId() {
		return this.last_clip_id++;
	}

	public void add(Clip clip) {
		long id = this.returnNextId();
		clip.setId(id);
		clip_order.add(id);
		clips.put(id, clip);
	}

	public void add(int index, Clip clip) {
		long id = this.returnNextId();
		clip.setId(id);
		clip_order.add(index, id);
		clips.put(id, clip);
	}

	public void clear() {
		clip_order.clear();
		clips.clear();
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

	Clip get(long id) {
		return clips.get(id);
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
	}

	public void remove(Clip clip) {
		long remove_id = clip.getId();
		clips.remove(remove_id);
		clip_order.remove(remove_id);
	}

	public int size() {
		return clip_order.size();
	}

	public Clip[] toArray() {
		return (Clip[]) clips.values().toArray();
	}

	class PlaylistIterator implements Iterator<Clip> {

		Playlist playlist;
		Iterator<Long> order_iterator;
		Clip current_clip;

		public PlaylistIterator(Playlist playlist) {
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
			this.order_iterator.remove();
		}

	}
}
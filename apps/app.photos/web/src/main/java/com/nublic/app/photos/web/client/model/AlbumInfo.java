package com.nublic.app.photos.web.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

public class AlbumInfo {
	private long id;
	private AlbumOrder order;
	private long rowCount;
	private Map<Long, PhotoInfo> photos;
	
	private Object lock = new Object();
	
	public AlbumInfo(long id, AlbumOrder order) {
		this.id = id;
		this.order = order;
		this.rowCount = -1;
		this.photos = new HashMap<Long, PhotoInfo>();
	}
	
	public long getId() {
		return id;
	}
	
	public AlbumOrder getOrder() {
		return order;
	}
	
	public long getRowCount() {
		return rowCount;
	}
	
	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;
	}
	
	public boolean has(long start, long length) {
		for (long i = 0; i < length; i++) {
			if (!photos.containsKey(start + i))
				return false;
		}
		return true;
	}
	
	public PhotoInfo get(long position) {
		return photos.get(position);
	}
	
	public List<PhotoInfo> get(long start, long length) {
		ArrayList<PhotoInfo> p = new ArrayList<PhotoInfo>();
		for (long i = 0; i < length; i++) {
			if (photos.containsKey(start + i)) {
				p.add(get(start + i));
			}
		}
		return p;
	}
	
	public  void addPhoto(long position, PhotoInfo photo) {
		synchronized(lock) {
			this.photos.put(position, photo);
		}
	}
	
	public void removePhoto(final long position) {
		synchronized(lock) {
			this.photos.remove(position);
			// Update keys
			// Get keys to update
			Map<Long, PhotoInfo> f = Maps.filterKeys(this.photos, new Predicate<Long>() {
				@Override
				public boolean apply(Long k) {
					return k > position;
				}
			});
			// Remove keys
			for (long k : f.keySet()) {
				this.photos.remove(k);
			}
			// Add new keys
			for (Entry<Long, PhotoInfo> p : f.entrySet()) {
				this.photos.put(p.getKey() - 1, p.getValue());
			}
		}
	}
	
	/* public void replacePosition(long oldPos, long newPos) {
		
	} */
}

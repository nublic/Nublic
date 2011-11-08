package com.nublic.app.manager.web.client;

public class LocationWithHash {
	String base;
	String hash;
	
	public LocationWithHash(String url) {
		int hashIndex = url.indexOf('#');
		if (hashIndex == -1) {
			base = url;
			hash = "";
		} else {
			base = url.substring(0, hashIndex);
			hash = url.substring(hashIndex + 1);
		}
	}
	
	public LocationWithHash(String base, String hash) {
		this.base = base;
		this.hash = hash;
	}
	
	public boolean sameBase(LocationWithHash other) {
		return base.equals(other.getBase());
	}
	
	public boolean sameHash(LocationWithHash other) {
		return hash.equals(other.getHash());
	}
	
	public String getBase() {
		return base;
	}

	public String getHash() {
		return hash;
	}
	
	public String getLocation() {
		return hash.isEmpty() ? base : base + "#" + hash;
	}
}

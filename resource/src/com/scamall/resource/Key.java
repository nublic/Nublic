package com.scamall.resource;

import java.util.HashSet;

public class Key {

	private long keyId;

	private long typeId;

	private long appId;

	private HashSet<Value> values = new HashSet<Value>();
	
	/**
	 * @return the keyId
	 */
	public long getKeyId() {
		return keyId;
	}

	/**
	 * @param keyId
	 *            the keyId to set
	 */
	public void setKeyId(long keyId) {
		this.keyId = keyId;
	}

	/**
	 * @return the typeId
	 */
	public long getTypeId() {
		return typeId;
	}

	/**
	 * @param typeId
	 *            the typeId to set
	 */
	public void setTypeId(long typeId) {
		this.typeId = typeId;
	}

	/**
	 * @return the appId
	 */
	public long getAppId() {
		return appId;
	}

	/**
	 * @param appId
	 *            the appId to set
	 */
	public void setAppId(long appId) {
		this.appId = appId;
	}

	/**
	 * @return the values
	 */
	public HashSet<Value> getValues() {
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(HashSet<Value> values) {
		this.values = values;
	}

}

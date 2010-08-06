package com.scamall.resource;

public class Resource {

	private long id;

	private long typeId;
	
	private String resourceId;

	/**
	 * @return the internal id of the table
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the typeId
	 */
	public long getTypeId() {
		return typeId;
	}
	
	/**
	 * @param typeId the typeId to set
	 */
	public void setTypeId(long typeId) {
		this.typeId = typeId;
	}

	/**
	 * @return the resourceId
	 */
	public String getResourceId() {
		return resourceId;
	}

	/**
	 * @param resourceId the resourceId to set
	 */
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

}

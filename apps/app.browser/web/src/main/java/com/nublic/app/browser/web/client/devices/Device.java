package com.nublic.app.browser.web.client.devices;

public class Device {
	int id;
	DeviceKind kind;
	String name;
	boolean owner;

	public Device(int id, DeviceKind kind, String name, boolean owner) {
		this.id = id;
		this.kind = kind;
		this.name = name;
		this.owner = owner;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DeviceKind getKind() {
		return kind;
	}

	public void setKind(DeviceKind kind) {
		this.kind = kind;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isOwner() {
		return owner;
	}

	public void setOwner(boolean owner) {
		this.owner = owner;
	}
	
}

// Copyright (c) 2011, Nublic

/** 
 * @author Alejandro Serrano Mena
 */
package com.nublic.resource.java;

public class App {
	String name;
	
	public App(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Key getKey(String name) {
		return new Key(this, name);
	}
}

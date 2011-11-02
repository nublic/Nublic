package com.nublic.util.lattice;

import java.util.Collection;

public class LatticeElement {
	int id;
	Collection<Integer> greater;
	
	public LatticeElement(int id, Collection<Integer> greater) {
		this.id = id;
		this.greater = greater;
	}
	
	@Override
	public String toString() {
		return Integer.toString(id);
	}
}

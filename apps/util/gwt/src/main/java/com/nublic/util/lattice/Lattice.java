package com.nublic.util.lattice;

import java.util.List;

public interface Lattice<T> {
	void insert(T t);
	void remove(T t);
	boolean contains(T t);
	List<T> elementsGreaterThan(T t);
	List<T> elementsLessThan(T t);
}

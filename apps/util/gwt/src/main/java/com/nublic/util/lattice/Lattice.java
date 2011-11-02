package com.nublic.util.lattice;

import java.util.Set;

public interface Lattice<T> {
	void insert(T t);
	void remove(T t);
	Set<T> elementsGreaterThan(T t);
	Set<T> elementsLessThan(T t);
}

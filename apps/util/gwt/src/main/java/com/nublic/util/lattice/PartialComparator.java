package com.nublic.util.lattice;

public interface PartialComparator<T> {
	Ordering compare(T a, T b);
}

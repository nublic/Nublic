package com.nublic.util.lattice;

public class InverseComparator<T> implements PartialComparator<T> {

	PartialComparator<T> comparator;
	
	public InverseComparator(PartialComparator<T> comparator) {
		this.comparator = comparator;
	}
	
	@Override
	public Ordering compare(T a, T b) {
		return comparator.compare(a, b).inverse();
	}

}

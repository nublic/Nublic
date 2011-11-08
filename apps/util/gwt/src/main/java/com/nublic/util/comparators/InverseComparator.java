package com.nublic.util.comparators;

import java.util.Comparator;

public class InverseComparator<T> implements Comparator <T> {
	Comparator<? super T> comp;

	public InverseComparator(Comparator<? super T> comp) {
		this.comp = comp;
	}

	@Override
	public int compare(T o1, T o2) {
		return comp.compare(o2, o1);
	}

}

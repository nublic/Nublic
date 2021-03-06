package com.nublic.util.lattice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public class SimpleLattice<T> implements Lattice<T> {
	
	ArrayList<T> elements;
	PartialComparator<T> comparator;
	
	public SimpleLattice(PartialComparator<T> comparator) {
		this.elements = Lists.newArrayList();
		this.comparator = comparator;
	}

	@Override
	public void insert(T t) {
		this.elements.add(t);
	}

	@Override
	public void remove(T t) {
		this.elements.remove(t);
	}
	
	@Override
	public boolean contains(T t) {
		return this.elements.contains(t);
	}

	@Override
	public List<T> elementsGreaterThan(T t) {
		return elementsThat(t, Ordering.GREATER);
	}

	@Override
	public List<T> elementsLessThan(T t) {
		return elementsThat(t, Ordering.LESS);
	}
	
	private List<T> elementsThat(final T t, final Ordering o) {
		Collection<T> filtered = Collections2.filter(elements, new Predicate<T>() {
			@Override
			public boolean apply(T e) {
				return comparator.compare(e, t) == o;
			}
		});
		return Lists.newArrayList(filtered);
	}

}

package com.nublic.util.lattice;

public class LatticeElementComparator implements PartialComparator<LatticeElement> {

	@Override
	public Ordering compare(LatticeElement a, LatticeElement b) {
		if (a.id == b.id) {
			return Ordering.EQUAL;
		} else if (b.greater.contains(a.id)) {
			return Ordering.GREATER;
		} else if (a.greater.contains(b.id)) {
			return Ordering.LESS;
		} else {
			return Ordering.INCOMPARABLE;
		}
	}

}

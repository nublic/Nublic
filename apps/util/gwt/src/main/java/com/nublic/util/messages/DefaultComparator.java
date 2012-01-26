package com.nublic.util.messages;

import com.nublic.util.lattice.Ordering;
import com.nublic.util.lattice.PartialComparator;

public class DefaultComparator implements PartialComparator<Message> {
	public static DefaultComparator INSTANCE = new DefaultComparator();

	@Override
	public Ordering compare(Message a, Message b) {
		if (a.getSequenceNumber() > b.getSequenceNumber()) {
			return Ordering.GREATER;
		} else if (a.getSequenceNumber() == b.getSequenceNumber()) {
			return Ordering.EQUAL;
		} else {
			return Ordering.LESS;
		}
	}

}

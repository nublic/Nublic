package com.nublic.util.messages;

import com.google.gwt.http.client.RequestBuilder.Method;
import com.nublic.util.lattice.PartialComparator;

public class SequenceIgnorerTester<M extends Message> extends SequenceIgnorer<M> {

	public SequenceIgnorerTester(PartialComparator<M> comparator) {
		super(comparator);
	}

	@Override
	protected void performSend(final M message, Method method) {
		
	}
	
}

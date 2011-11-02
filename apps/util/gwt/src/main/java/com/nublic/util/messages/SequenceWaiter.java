package com.nublic.util.messages;

import com.google.gwt.http.client.Response;
import com.nublic.util.lattice.PartialComparator;

public class SequenceWaiter<M extends Message> extends SequenceHelper<M> {
	// Inherited
//	protected Object lock = new Object();
//	protected Lattice<M> messageLattice;
	
	public SequenceWaiter(PartialComparator<M> comparator) {
		super(comparator);
	}

	@Override
	public void actionOnError(M message) {
//		synchronized (lock) {
//			
//
//		}
		message.onError();
	}

	@Override
	public void actionOnSuccess(M message, Response response) {
		message.onSuccess(response);
	}

}

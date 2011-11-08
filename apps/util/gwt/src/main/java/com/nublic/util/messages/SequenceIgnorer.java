package com.nublic.util.messages;

import java.util.List;

import com.google.gwt.http.client.Response;
import com.nublic.util.lattice.PartialComparator;

public class SequenceIgnorer<M extends Message> extends SequenceHelper<M> {
	// Inherited
//	protected Object lock = new Object();
//	protected Lattice<M> messageLattice;

	public SequenceIgnorer(PartialComparator<M> comparator) {
		super(comparator);
	}

	@Override
	public void actionOnError(M message) {
		synchronized (lock) {
			List<M> greaters = messageLattice.elementsGreaterThan(message);
			List<M> lessers = messageLattice.elementsLessThan(message);
			for (M mes : lessers) {
				// Ignore messages with less priority
				mes.onIgnored();
				messageLattice.remove(mes);
			}
			if (greaters.isEmpty()) {
				// No messages found with more priority, show error
				message.onError();
			} else {
				// There is an error in this message, but we're waiting for one with more priority
				// We let the greatest priority message to act (show error or success)
				message.onIgnored();
			}
			messageLattice.remove(message);
		}
	}

	@Override
	public void actionOnSuccess(M message, Response response) {
		synchronized (lock) {
			List<M> lessers = messageLattice.elementsLessThan(message);
			for (M mes : lessers) {
				// Ignore messages with less priority
				mes.onIgnored();
				messageLattice.remove(mes);
			}
			// We'll show the success dependlessly on the greater messages we're waiting for 
			message.onSuccess(response);
			messageLattice.remove(message);
		}
	}

}

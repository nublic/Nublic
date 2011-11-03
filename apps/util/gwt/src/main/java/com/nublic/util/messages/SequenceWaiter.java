package com.nublic.util.messages;

import java.util.List;
import java.util.Set;

import com.google.gwt.http.client.Response;
import com.nublic.util.graph.DefaultEdge;
import com.nublic.util.graph.DirectedSubgraph;
import com.nublic.util.lattice.PartialComparator;

public class SequenceWaiter<M extends Message> extends SequenceHelper<M> {
	// Inherited
//	protected Object lock = new Object();
//	protected GraphLattice<M> messageLattice;

	public SequenceWaiter(PartialComparator<M> comparator) {
		super(comparator);
	}

	@Override
	public void actionOnError(M message) {
		synchronized (lock) {
			List<M> greaters = messageLattice.elementsGreaterThan(message);
			List<M> lessers = messageLattice.elementsLessThan(message);
			if (greaters.isEmpty()) {
				// If we're not waiting for a more priority event we fire ourselves .. 
				message.onError();
				// ... and remove the less priority events waiting
				for (M mes : lessers) {
					mes.onIgnored();
					messageLattice.remove(mes);
				}
				messageLattice.remove(message);
			} else {
				message.setError(true);
			}
		}
	}

	@Override
	public void actionOnSuccess(M message, Response response) {
		synchronized (lock) {
			//List<M> lessers = messageLattice.elementsLessThan(message);
			// necesary to get the graph to fire the events properly
			DirectedSubgraph<M, DefaultEdge> lessers = messageLattice.subgraphLessThan(message);
			List<M> greaters = messageLattice.elementsGreaterThan(message);
			if (greaters.isEmpty()) {
				// If we're not waiting for a more priority event we fire ourselves
				message.saveResponse(response); // To let the graph structure fire the current event
				fireEvents(lessers, message, false);
			} else {
				// else we save the result and wait for a more priority event to fire us
				message.saveResponse(response);
			}
		}
	}

	private void fireEvents(DirectedSubgraph<M, DefaultEdge> lessers, M root, boolean ignoreTail) {
		boolean ignore;
		boolean fired;
		if (ignoreTail) {
			// On error we'll show the error in the one with more priority and then ignore the tail
			root.onIgnored();
			fired = true;
			ignore = true;
		} else {
			// We fire the event corresponding to the root and then recursively we'll fire the rest waiting events
			fired = root.fireEvent();
			ignore = false;
		}
		if (fired) {
			if (root.hasError()) {
				// In case of error we'll ignore the tail
				ignore = true;
			}
			Set<DefaultEdge> pointing = lessers.outgoingEdgesOf(root);
			for (DefaultEdge edge : pointing) {
				// To fire the waiting events
				M child = lessers.getEdgeTarget(edge);
				fireEvents(messageLattice.subgraphLessThan(root), child, ignore);
			}
			messageLattice.remove(root);
		}
	}

}

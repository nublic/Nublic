package com.nublic.util.messages;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.util.lattice.GraphLattice;
import com.nublic.util.lattice.PartialComparator;

public abstract class SequenceHelper <M extends Message> {
	protected Object lock;
	private long lastSequenceNumber;
	protected GraphLattice<M> messageLattice;
//	protected ArrayList<M> messageList = new ArrayList<M>();

	SequenceHelper(PartialComparator<M> comparator) {
		lock = new Object();
		lastSequenceNumber = 0;
		messageLattice = new GraphLattice<M>(comparator);		
	}
	
	public void send(final M message, RequestBuilder.Method method) {
		String url = URL.encode(message.getURL());
		RequestBuilder builder = new RequestBuilder(method, url);
		
		// Assigns a sequence number and adds the message to the list safely
		synchronized (lock) {
			message.setSequenceNumber(lastSequenceNumber);
			messageLattice.insert(message);
			lastSequenceNumber++;
		}
		
		try {
			@SuppressWarnings("unused")
			// It is not unused, we maintain callbacks
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					if (messageLattice.contains(message)) {
						// Otherwise the message has been ignored
						actionOnError(message);
					}
				}
				public void onResponseReceived(Request request, Response response) {
					if (messageLattice.contains(message)) {
						// Otherwise the message has been ignored
						actionOnSuccess(message, response);
					}
				}
			});
		} catch (RequestException e) {
			message.onError();
		}
	}

	public abstract void actionOnError(M message);
	public abstract void actionOnSuccess(M message, Response response);

}

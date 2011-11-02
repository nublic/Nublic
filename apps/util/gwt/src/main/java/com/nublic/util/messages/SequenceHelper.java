package com.nublic.util.messages;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.util.lattice.Lattice;
import com.nublic.util.lattice.PartialComparator;
import com.nublic.util.lattice.SimpleLattice;

public abstract class SequenceHelper <M extends Message> {
	protected Object lock;
	private long lastSequenceNumber;
	protected Lattice<M> messageLattice;
//	protected ArrayList<M> messageList = new ArrayList<M>();

	SequenceHelper(PartialComparator<M> comparator) {
		lock = new Object();
		lastSequenceNumber = 0;
		messageLattice = new SimpleLattice<M>(comparator);		
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
					actionOnError(message);
//					message.onError();
				}
				public void onResponseReceived(Request request, Response response) {
					actionOnSuccess(message, response);
//					message.onSuccess();
				}
			});
		} catch (RequestException e) {
			message.onError();
		}
	}

	public abstract void actionOnError(M message);
	public abstract void actionOnSuccess(M message, Response response);

}

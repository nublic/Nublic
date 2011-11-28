package com.nublic.util.messages;

import java.util.HashMap;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.util.lattice.GraphLattice;
import com.nublic.util.lattice.Ordering;
import com.nublic.util.lattice.PartialComparator;

public abstract class SequenceHelper <M extends Message> {
	protected Object lock;
	private long lastSequenceNumber;
	protected GraphLattice<M> messageLattice;

	SequenceHelper(PartialComparator<M> comparator) {
		lock = new Object();
		lastSequenceNumber = 0;
		messageLattice = new GraphLattice<M>(comparator);		
	}
	
	public void send(final M message, RequestBuilder.Method method) {
		
		// Assigns a sequence number and adds the message to the list safely
		synchronized (lock) {
			message.setSequenceNumber(lastSequenceNumber);
			messageLattice.insert(message);
			lastSequenceNumber++;
		}

		performSend(message, method);
	}

	protected void performSend(final M message, Method method) {
		String url = URL.encode(message.getURL());
		RequestBuilder builder = new RequestBuilder(method, url);

		try {
			StringBuilder postData = new StringBuilder();
			
			if (method.equals(RequestBuilder.POST)) {
				builder.setHeader("Content-type", "application/x-www-form-urlencoded");
				
				HashMap<String, String> params = message.getParams();
				for (String key : params.keySet()) {
					if (postData.length() != 0) {
						postData.append("&");
					}
					postData.append(key);
					postData.append("=");
					postData.append(params.get(key));
				}				
			}
			
			@SuppressWarnings("unused")
			// It is not unused, we maintain callbacks
			Request request = builder.sendRequest(postData.toString(), new RequestCallback() {
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
	
	public static <M extends Message> void sendJustOne(M message, RequestBuilder.Method method) {
		SequenceIgnorer<M> queue = new SequenceIgnorer<M>(new PartialComparator<M>() {
			@Override
			public Ordering compare(M a, M b) {
				return a.equals(b) ? Ordering.EQUAL : Ordering.INCOMPARABLE;
			}
		});
		queue.send(message, method);
	}

}

package com.nublic.util.messages;
import com.google.gwt.http.client.Response;

public abstract class Message {
	private long sequenceNumber;
	
	public abstract String getURL();
	public abstract void onSuccess(Response response);
	public abstract void onError();
	
	public void onIgnored() {
		// Override this function to take actions when the message is ignored
	}

	public void setSequenceNumber(long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public long getSequenceNumber() {
		return sequenceNumber;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Message)){
			return false;
		} else {
			return ((Message) o).getSequenceNumber() == sequenceNumber; 
		}
	}
//	public int compareTo();
}

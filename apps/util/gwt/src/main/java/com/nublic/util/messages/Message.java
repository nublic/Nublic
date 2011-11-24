package com.nublic.util.messages;
import java.util.HashMap;

import com.google.gwt.http.client.Response;

public abstract class Message {
	boolean error = false;
	private long sequenceNumber;
	Response savedResponse;
	HashMap<String, String> params = new HashMap<String, String>();
	
	public abstract String getURL();
	public abstract void onSuccess(Response response);
	public abstract void onError();
	
	public void onIgnored() {
		// Override this function to take actions when the message is ignored
	}

	public void addParam(String key, String value) {
		params.put(key, value);
	}
	
	public HashMap<String, String> getParams() {
		return params;
	}
	
	public void setSequenceNumber(long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public long getSequenceNumber() {
		return sequenceNumber;
	}
	
	public boolean hasError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}
	
	public void saveResponse(Response response) {
		this.savedResponse = response;
	}

	public void useSavedResponse() {
		onSuccess(savedResponse);
	}

	public boolean isReady() {
		return !(savedResponse == null);
	}
	
	// Fires the event (error or success) when it is ready and returns true
	// if it's not ready returns false
	public boolean fireEvent() {
		if (error) {
			onError();
			return true;
		} else if (isReady()) {
			onSuccess(savedResponse);
			return true;
		} else {
			return false;
		}
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

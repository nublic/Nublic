package com.nublic.util.messages;

import com.google.gwt.http.client.Response;
import com.nublic.util.lattice.Ordering;
import com.nublic.util.lattice.PartialComparator;

public class MessageForTesting extends Message {
	private int branch;
	private String url;
	
	public MessageForTesting(int branch, String url) {
		this.branch = branch;
		this.url = url;
	}
	
	public int getBranch() {
		return branch;
	}

	public void setBranch(int branch) {
		this.branch = branch;
	}

	public static class Comparator implements PartialComparator<MessageForTesting> {
		@Override
		public Ordering compare(MessageForTesting a, MessageForTesting b) {
			if (a.getBranch() != b.getBranch()) {
				return Ordering.INCOMPARABLE;
			} else {
				if (a.getSequenceNumber() > b.getSequenceNumber()) {
					return Ordering.GREATER;
				} else {
					return Ordering.LESS;
				}
			}
		}
	}
	
	@Override
	public String getURL() {
		return url;
	}

	@Override
	public void onSuccess(Response response) {
		url += " - success";
	}

	@Override
	public void onError() {
		url += " - error";
	}

}

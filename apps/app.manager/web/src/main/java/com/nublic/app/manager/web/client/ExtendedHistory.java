package com.nublic.app.manager.web.client;

import java.util.Stack;

public class ExtendedHistory {
	Stack<String> previous;
	String current;
	Stack<String> next;
	boolean bare_new;
	
	public ExtendedHistory(String url) {
		this.previous = new Stack<String>();
		this.current = url.replace("%23", "#");
		this.next = new Stack<String>();
		this.bare_new = true;
	}
	
	public boolean isBareNew() {
		return this.bare_new;
	}
	
	public boolean isPrevious(String url) {
		if (this.previous.isEmpty())
			return false;
		return this.previous.peek().equals(url.replace("%23", "#"));
	}
	
	public boolean isCurrent(String url) {
		return this.current.equals(url.replace("%23", "#"));
	}
	
	public boolean isNext(String url) {
		if (this.next.isEmpty())
			return false;
		return this.next.peek().equals(url.replace("%23", "#"));
	}
	
	public void back() {
		next.push(current);
		current = previous.pop();
		bare_new = false;
	}
	
	public void forward() {
		previous.push(current);
		current = next.pop();
		bare_new = false;
	}
	
	public void go(String url) {
		previous.push(current);
		current = url.replace("%23", "#");
		next.clear();
		bare_new = false;
	}
}

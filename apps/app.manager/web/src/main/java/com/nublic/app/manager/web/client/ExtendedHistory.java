package com.nublic.app.manager.web.client;

import java.util.Stack;

import com.nublic.app.manager.web.frame.Counter;
import com.nublic.util.tuples.Pair;

public class ExtendedHistory {
	Stack<Pair<Long, String>> previous;
	Pair<Long, String> current;
	Stack<Pair<Long, String>> next;
	boolean bare_new;
	
	public ExtendedHistory(String url) {
		this.previous = new Stack<Pair<Long, String>>();
		this.current = new Pair<Long, String>(Counter.NOT_ALLOWED, url.replace("%23", "#"));
		this.next = new Stack<Pair<Long, String>>();
		this.bare_new = true;
	}
	
	public boolean isBareNew() {
		return this.bare_new;
	}
	
	public boolean isPrevious(String url) {
		if (this.previous.isEmpty())
			return false;
		return this.previous.peek()._2.equals(url.replace("%23", "#"));
	}
	
	public boolean isCurrent(String url) {
		return this.current._2.equals(url.replace("%23", "#"));
	}
	
	public boolean isNext(String url) {
		if (this.next.isEmpty())
			return false;
		return this.next.peek()._2.equals(url.replace("%23", "#"));
	}
	
	public boolean isPreviousId(long id) {
		if (this.previous.isEmpty())
			return false;
		return this.previous.peek()._1 == id;
	}
	
	public boolean isCurrentId(long id) {
		return this.current._1 == id;
	}
	
	public boolean isNext(long id) {
		if (this.next.isEmpty())
			return false;
		return this.next.peek()._1 == id;
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
	
	public void go(String url, long id) {
		previous.push(current);
		current = new Pair<Long, String>(id, url.replace("%23", "#"));
		next.clear();
		bare_new = false;
	}
}

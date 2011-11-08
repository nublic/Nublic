package com.nublic.util.tuples;

public class Pair<A, B> {
	public A _1;
	public B _2;
	
	public Pair(A a, B b) {
		this._1 = a;
		this._2 = b;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Pair))
			return false;
		
		Pair<?, ?> p = (Pair<?, ?>)obj;
		return _1.equals(p._1) && _2.equals(p._2);
	}
}

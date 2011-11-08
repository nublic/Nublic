package com.nublic.util.tuples;

public class Tuple3<A, B, C> {
	public A _1;
	public B _2;
	public C _3;
	
	public Tuple3(A a, B b, C c) {
		this._1 = a;
		this._2 = b;
		this._3 = c;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Tuple3))
			return false;
		
		Tuple3<?, ?, ?> p = (Tuple3<?, ?, ?>)obj;
		return _1.equals(p._1) && _2.equals(p._2) && _3.equals(p._3);
	}
}

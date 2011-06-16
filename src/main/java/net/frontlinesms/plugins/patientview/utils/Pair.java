package net.frontlinesms.plugins.patientview.utils;

public class Pair<A,B> {

	public final A one;
	public final B two;
	
	public Pair(A a, B b){
		this.one = a;
		this.two = b;
	}
	
	public String toString() {
        return "(" + one + ", " + two + ")";
    }
	
    private static boolean equals(Object x, Object y) {
    	return (x == null && y == null) || (x != null && x.equals(y));
    }

    public boolean equals(Object other) {
   	return
	    other instanceof Pair &&
	    equals(one, ((Pair)other).one) &&
	    equals(two, ((Pair)other).two);
    }

    public int hashCode() {
	if (one == null) return (two == null) ? 0 : two.hashCode() + 1;
	else if (two == null) return one.hashCode() + 2;
	else return one.hashCode() * 17 + two.hashCode();
    }
}

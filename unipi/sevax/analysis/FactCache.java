/*
 * Copyright (c) 2010-2011 Brigham Young University
 * 
 * This file is part of the BYU RapidSmith Tools.
 * 
 * BYU RapidSmith Tools is free software: you may redistribute it 
 * and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * BYU RapidSmith Tools is distributed in the hope that it will be 
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * A copy of the GNU General Public License is included with the BYU 
 * RapidSmith Tools. It can be found at doc/gpl2.txt. You may also 
 * get a copy of the license at <http://www.gnu.org/licenses/>.
 * 
 */
package unipi.sevax.analysis;

import java.math.BigInteger;
import java.util.Vector;

public class FactCache {
//	public static final BigInteger[] factCache = new BigInteger[700];
	protected static Vector<BigInteger> table = new Vector<BigInteger>();       // create cache
	static { table.addElement(BigInteger.valueOf(1)); } // initialize 1st element

	public BigInteger getFactorial(int x) {
	return factorial(x);
}
	
	
	/**
	 * This version of the program uses arbitrary precision integers, so it does
	 * not have an upper-bound on the values it can compute.  It uses a Vector
	 * object to cache computed values instead of a fixed-size array.  A Vector
	 * is like an array, but can grow to any size.  The factorial() method is
	 * declared "synchronized" so that it can be safely used in multi-threaded
	 * programs.  Look up java.math.BigInteger and java.util.Vector while 
	 * studying this class.
	 **/
	
	/** The factorial() method, using BigIntegers cached in a Vector */
	public static synchronized BigInteger factorial(int x) { //

		if(x < 0) {
			//System.out.println(x);
			x = 1;
		}
		
		if (x < 0) throw new IllegalArgumentException("x must be non-negative.");
	    for(int size = table.size(); size <= x; size++) {
	      BigInteger lastfact = (BigInteger)table.elementAt(size-1);
	      BigInteger nextfact = lastfact.multiply(BigInteger.valueOf(size));
	      table.addElement(nextfact);
	    }
		return (BigInteger) table.elementAt(x);
	 }


}

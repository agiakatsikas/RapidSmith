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
package unipi.sevax.utilities;

import java.math.BigInteger;
import java.util.Vector;

import unipi.sevax.analysis.FactCache;

/**
 * This class Math contains static methods for performing mathematical functions needed from the unipi.placer package and the unipi.analysis package.
 * @author Aitzan Sari, Dimitris Agiakatsikas.
 * Created on: Oct 26, 2013
 */
public class Maths {

	/**
	 * A static cache for saving calculated factorial numbers. 
	 */
	static FactCache factCache = new FactCache();
	
	protected static Vector<Double> table = new Vector<Double>();       // create cache
	static { table.addElement(1D); } // initialize 1st element
	 /**The cross_count is the q(i) factor used to compensate the wire length model in the linear congestion cost function 
	 * of the the simulated annealing based placement
	 */
	static final double[] cross_count = {	/* [0..49] */
		    1.0, 1.0, 1.0, 1.0828, 1.1536, 1.2206, 1.2823, 1.3385, 1.3991, 1.4493,
		    1.4974, 1.5455, 1.5937, 1.6418, 1.6899, 1.7304, 1.7709, 1.8114, 1.8519,
		    1.8924,
		    1.9288, 1.9652, 2.0015, 2.0379, 2.0743, 2.1061, 2.1379, 2.1698, 2.2016,
		    2.2334,
		    2.2646, 2.2958, 2.3271, 2.3583, 2.3895, 2.4187, 2.4479, 2.4772, 2.5064,
		    2.5356,
		    2.5610, 2.5864, 2.6117, 2.6371, 2.6625, 2.6887, 2.7148, 2.7410, 2.7671,
		    2.7933
	};
		
	/**
	 * This version of the program uses arbitrary precision integers, so it does
	 * not have an upper-bound on the values it can compute.  It uses a Vector
	 * object to cache computed values instead of a fixed-size array.  A Vector
	 * is like an array, but can grow to any size.  The factorial() method is
	 * declared "synchronized" so that it can be safely used in multi-threaded
	 * programs.  Look up java.math.BigInteger and java.util.Vector while 
	 * studying this class.
	 * The factorial() method, using BigIntegers cached in a Vector
	 * @param x
	 * @return double
	 */
	private static synchronized double factorial(int x) { //
		if (x < 0) throw new IllegalArgumentException("x must be non-negative.");
	    for(int size = table.size(); size <= x; size++) {
	    	Double lastfact = (Double)table.elementAt(size-1);
	    	Double nextfact = lastfact * size;
	      table.addElement(nextfact);
	    }
		return (Double) table.elementAt(x);
	 }
    
	
    /**
     * This method is used for finding combinations in probability theory. The method tries to calculate the factorial with double numbers and if the results are out of the double range,
     * it re-calculates the factorial with BigIntegers. This technique in combination with cashing  speedups the calculations.
	 * p(n,m)
	 * @param n 
	 * @param m
	 * @return double 
	 */
	public static Double calcPnm(int n, int m){
		Double value =(factorial(n)/(factorial(m) * factorial(n - m)));
		if(value.equals(Double.NaN)) {
			value = calcPnmBigInt(n, m);
		} 
		if(value.isInfinite()) {
			value = calcPnmBigInt(n, m);
		} 
		return value;	
	}
	
    /**
     * This method is used for finding combinations in probability theory. It uses BigInteger numbers in the factorial calculation procedure.
	 * p(n,m)
	 * @param n
	 * @param m
	 * @return double 
	 */
	public static double calcPnmBigInt(int n, int m) {
		BigInteger f1 = factCache.getFactorial(n);
		BigInteger f2 = factCache.getFactorial(m);
		BigInteger f3 = factCache.getFactorial(n - m);
		BigInteger f4 = f2.multiply(f3);
		
		BigInteger result = f1.divide( f4 );

		return result.doubleValue();
		
	}
	
	/**
	 * Needed to find the usage probability of switch matrices that will be used for the routing of a design.
	 * This method is called from the unipi.analysis package for a more accurately estimation of the short bits.
	 * p(i, j, m, n)
	 * @param i 
	 * @param j
	 * @param m
	 * @param n 
	 * @return Double 
	 */
	public static double calcPijmn(int i, int j, int m,int n, int bboxTerminal) {
		//int gcd = gcd(gcd(i,j) , gcd(m,n));
		//i = i / gcd;
		//j = j / gcd;
		//m = m / gcd;
		//n = n / gcd;
//		p1 = calcPnm((i + j - 2), (j -1));
//		p2 = calcPnm((m + n - (i + j)), (n - j));
//		p3 = calcPnm((m + n - 2), (m - 1));
//		//return ((p1*p2)/p3);
//		return ( getCrossCount(bboxTerminal) * ((p1 * p2) / p3) );
		
		return ( getCrossCount(bboxTerminal) * ((calcPnm((i + j - 2), (j -1)) * calcPnm((m + n - (i + j)), (n - j))) / calcPnm((m + n - 2), (m - 1))) );
	}
	
	/**
	 * Greatest Common Divisor between two integers.
	 * @param a
	 * @param b
	 * @return int
	 */
	public static int gcd(int a, int b) {
	  if(a == 0 || b == 0) return a+b; // base case
	  return gcd(b,a%b);
	}
	
	/**
	 * Return the q(i). Values from 1.0 to 2.7933 depending on nets terminals
	 * @param terminals
	 * @return double
	 */
	public static double getCrossCount(int terminals) {
		double crossing = 0;
		if((terminals) > 50)
		{
		    crossing = 2.7933 + 0.02616 * (terminals - 50);
		    /*    crossing = 3.0;    Old value  */
		}
	    else
		{
		    crossing = cross_count[terminals -1];
		}
		return crossing;
	}
	
	
	/**
	 * Standard deviation calculation 
	 * @param n
	 * @param sum_x_squared
	 * @param av_x
	 * @return double
	 */
	public static double get_std_dev(int n,
		    double sum_x_squared,
		    double av_x)
	{

	    /* Returns the standard deviation of data set x.  There are n sample points, *
	     * sum_x_squared is the summation over n of x^2 and av_x is the average x.   *
	     * All operations are done in double precision, since round off error can be *
	     * a problem in the initial temp. std_dev calculation for big circuits.      */

	    double std_dev;

	    if(n <= 1)
		std_dev = 0.;
	    else
		std_dev = (sum_x_squared - n * av_x * av_x) / (double)(n - 1);

	    if(std_dev > 0.)		/* Very small variances sometimes round negative */
		std_dev = Math.sqrt(std_dev);
	    else
		std_dev = 0.;

	    return (std_dev);
	}

}

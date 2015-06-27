/*
 * Copyright (c) 2013, SRI International
 * All rights reserved.
 * Licensed under the The BSD 3-Clause License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * 
 * http://opensource.org/licenses/BSD-3-Clause
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the aic-util nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.sri.ai.util.math;

import static com.sri.ai.util.Util.factorial;
import static com.sri.ai.util.Util.join;
import static com.sri.ai.util.Util.myAssert;

import java.util.Collections;
import java.util.List;

import com.google.common.primitives.Ints;

/**
 * Multinomials are objects representing a distribution of n objects into m classes, useful in combinatorial problems.
 * This class are such objects but also work as iterators generating all possible distributions
 * of a certain size of a set of objects, and calculating the multinomials number of each of them.
 * The method <code>next()</code> returns the same Multinomial instance on which it is invoked,
 * so you can either use the value returned by <code>next()</code> to get the distribution's properties or simply
 * invoke <code>next()</code> and use the iterator itself to get those properties. For example:
 *
 * <pre>
 * Multinomial m = new Multinomial(10,2);
 * while(m.hasNext()) {
 *     m.next();
 *     int class1Size = m.getClassSize(1);
 *     (...)
 * }
 * </pre>
 * 
 * To avoid modifying the original instance when iterate, use the clone method first.
 */
public class Multinomial implements Cloneable {
	
	//////////////////////////////////// PROPERTIES ////////////////////////////////

	private int n;
	/** Returns the number of objects being distributed. */
	public int getN() {
		return n;
	}

	private int m;
	/** Returns the number of classes in which objects are being distributed. */
	public int getM() {
		return m;
	}

	/** Number of objects in each class. */
	private int[] counters;
	
	/** Index in counters from which to consider the multinomial. */
	private int myIndex;

	/** Represents the distribution after myIndex. */
	private Multinomial subMultinomial;

	//////////////////////////////////// CONSTRUCTORS ////////////////////////////////

	/** Constructs a multinomial on n objects and m classes. */
	public Multinomial(int n, int m) {
		this(makeCounters(n, m));
	}

	/** Makes an array [0, ..., 0, n], the first distribution for a multinomial on n and m. */
	private static int[] makeCounters(int n, int m) {
		int[] counters = new int[m];
		placeAllElementsInLastClass(counters, 0, n);
		return counters;
	}

	/**
	 * Stores counters on given array, starting from offset <code>from</code>,
	 * so that all <code>n</code> elements are placed in the last class;
	 * @param counters
	 * @param from
	 * @param n
	 */
	private static void placeAllElementsInLastClass(int[] counters, int from, int n) {
		myAssert(() -> counters.length > 0, () -> "number of classes must be greater than 0.");
		for (int i = from; i != counters.length - 1; i++) {
			counters[i] = 0;
		}
		counters[counters.length - 1] = n;
	}
	
	/**
	 * Constructs a multinomial on <code>(sum_i counters[i])</code> objects and <code>counters.length</code> classes,
	 * with the count per class stored in the array.
	 * The object keeps and may modify the array later.
	 */
	public Multinomial(int[] counters) {
		this(counters, 0);
	}

	private Multinomial(int[] counters, int from) {
		myAssert(() -> counters.length > 0, () -> "number of classes must be greater than 0.");
		myAssert(() -> from < counters.length, () -> "from must be less than counters length");
		if (from + 1 != counters.length) { // if there is more than one class
			this.subMultinomial = new Multinomial(counters, from + 1);
			this.n = counters[from] + subMultinomial.getN();
			this.m = subMultinomial.getM() + 1;
		}
		else {
			this.subMultinomial = null;
			this.n = counters[from];
			this.m = 1;
		}
		this.myIndex = from;
		this.counters = counters;
	}
	
	public Multinomial clone() {
		Multinomial result = new Multinomial(counters.clone());
		return result;
	}

	///////////////////////////////////////////// ITERATION ////////////////////////////////

	/**
	 * Iterates multinomial distribution from its current state to its successor and returns true,
	 * if there is a successor,
	 * or leaves it unchanged and returns false otherwise.
	 * <p>
	 * A single-classed distribution has no successor (the only possible single classed distribution
	 * is that which contains all <code>n</code> objects in the single class).
	 * <p>
	 * For a multi-class distribution <code>n_0,...,n_m</code>,
	 * the successor is <code>n_0, successor(n_1,...,n_m)</code> if
	 * the <code>m - 1</code>-classed distribution <code>n_1,...,n_m</code> has a successor,
	 * or <code>n_0 + 1, first distribution of Multinomial(n - (n_0 + 1), m - 1)</code>
	 * if <code>n_0</code> is not <code>n</code>.
	 * Otherwise, it has no successor.
	 * 
	 * @return
	 */
	public boolean iterate() {
		boolean result;
		if (getM() == 1) {
			result = false;
		}
		else if (subMultinomial.iterate()) {
			result = true;
		}
		else if (counters[myIndex] != getN()) {
			counters[myIndex]++;
			placeAllElementsInLastClass(counters, myIndex + 1, subMultinomial.getN() - 1);
			subMultinomial = new Multinomial(counters, myIndex + 1);
			result = true;
		}
		else {
			result = false;
		}
		return result;
	}

	///////////////////////////////////// MULTINOMIAL COEFFICIENT ////////////////////////////////

	/** Returns the multinomial coefficient for this distribution. */
	public Rational choose() {
		Rational nFactorial = factorialOfN();
		Rational productOfFactorials = productOfFactorials();
		Rational result = nFactorial.divide(productOfFactorials);
		
//		System.out.println("choose of : " + join(Ints.asList(counters)));	
//		System.out.println("n : " + getN());	
//		System.out.println("m : " + getM());	
//		System.out.println("n!: " + nFactorial);	
//		System.out.println("prod of factorials: " + productOfFactorials);	
//		System.out.println("n! / prod of factorials: " + result);	

		return result;
	}

	private Rational factorialOfN = null;
	/** Calculates (at most once per object) the factorial of the number of objects. */
	private Rational factorialOfN() {
		if (factorialOfN == null) {
			factorialOfN = factorial(getN());
		}
		return factorialOfN;
	}

	/** Returns the product of factorials of classes sizes in this distribution. */
	public Rational productOfFactorials() {
		Rational result;
		if (getM() == 1) {
			result = factorialOfN();
		}
		else {
			Rational factorialOfMyFirstClassSize = factorial(counters[myIndex]);
			Rational productOfFactorialOfRemainingClassesSizes = subMultinomial.productOfFactorials();
			result = factorialOfMyFirstClassSize.multiply(productOfFactorialOfRemainingClassesSizes);
		}
		return result;
	}

	/////////////////////////////////// CLASS DISTRIBUTION ////////////////////////////////

	/**
	 * Returns a list with the class counters.
	 * @return
	 */
	public List<Integer> getCounters() {
		return Collections.unmodifiableList(Ints.asList(counters));
	}

	/** Returns the size class of a particular class. */
	public int getClassSize(int classIndex) {
		return counters[classIndex];
	}

	public int[] getClassSizes() {
		return counters;
	}

	///////////////////////////////////// TRIVIAL STUFF ////////////////////////////////

	@Override
	public String toString() {
		return "multinomial(" + join(Ints.asList(counters), ", ") + ")";
	}

	public void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
}

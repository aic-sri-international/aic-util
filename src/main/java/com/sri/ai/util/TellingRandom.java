package com.sri.ai.util;

import static com.sri.ai.util.Util.println;

import java.util.Random;

public class TellingRandom extends Random {
	private static final long serialVersionUID = 1L;
	private int counter = 0;

	public TellingRandom(long seed) {
		super(seed);
	}
	
	protected int next(int bits) {
		int result = super.next(bits);
		counter++;
		println("Generation " + counter + ": " + result);
		return result;
	}
}